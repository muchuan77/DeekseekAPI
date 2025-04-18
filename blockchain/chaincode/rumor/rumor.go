package main

import (
    "encoding/json"
    "fmt"
    "github.com/hyperledger/fabric/core/chaincode/shim"
    pb "github.com/hyperledger/fabric/protos/peer"
)

// RumorChaincode 谣言追踪链码
type RumorChaincode struct {
}

// Rumor 谣言结构
type Rumor struct {
    ID                 string         `json:"id"`
    Content           string         `json:"content"`
    Creator           string         `json:"creator"`
    CreateTime        string         `json:"createTime"`
    IsVerified        bool           `json:"isVerified"`
    Verifier          string         `json:"verifier"`
    VerifyTime        string         `json:"verifyTime"`
    VerificationComment string       `json:"verificationComment"`
    History           []HistoryRecord `json:"history"`
}

// HistoryRecord 历史记录结构
type HistoryRecord struct {
    Timestamp string `json:"timestamp"`
    Action    string `json:"action"`
    Operator  string `json:"operator"`
    Comment   string `json:"comment"`
}

// Init 初始化链码
func (t *RumorChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
    return shim.Success(nil)
}

// checkUserPermission 检查用户权限
func (t *RumorChaincode) checkUserPermission(stub shim.ChaincodeStubInterface, userId string, permission string) bool {
    // 调用权限管理合约检查权限
    args := [][]byte{[]byte("checkPermission"), []byte(userId), []byte(permission)}
    response := stub.InvokeChaincode("permission", args, "rumor-channel")
    
    if response.Status != shim.OK {
        return false
    }
    
    return string(response.Payload) == "true"
}

// Invoke 链码调用入口
func (t *RumorChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
    function, args := stub.GetFunctionAndParameters()
    
    switch function {
    case "createRumor":
        return t.createRumor(stub, args)
    case "verifyRumor":
        return t.verifyRumor(stub, args)
    case "getRumor":
        return t.getRumor(stub, args)
    case "getRumorHistory":
        return t.getRumorHistory(stub, args)
    default:
        return shim.Error("Invalid function name")
    }
}

// createRumor 创建谣言
func (t *RumorChaincode) createRumor(stub shim.ChaincodeStubInterface, args []string) pb.Response {
    if len(args) != 3 {
        return shim.Error("Incorrect number of arguments. Expecting 3")
    }

    id := args[0]
    content := args[1]
    creator := args[2]

    // 检查创建权限
    if !t.checkUserPermission(stub, creator, "CREATE_RUMOR") {
        return shim.Error("User does not have permission to create rumor")
    }

    // 检查谣言是否已存在
    rumorAsBytes, err := stub.GetState(id)
    if err != nil {
        return shim.Error(err.Error())
    }
    if rumorAsBytes != nil {
        return shim.Error("Rumor already exists")
    }

    // 创建谣言对象
    rumor := Rumor{
        ID:              id,
        Content:         content,
        Creator:         creator,
        CreateTime:      stub.GetTxTimestamp().String(),
        IsVerified:      false,
        History:         []HistoryRecord{},
    }

    // 添加创建记录
    historyRecord := HistoryRecord{
        Timestamp: stub.GetTxTimestamp().String(),
        Action:    "CREATE",
        Operator:  creator,
        Comment:   "Rumor created",
    }
    rumor.History = append(rumor.History, historyRecord)

    // 将谣言保存到账本
    rumorAsBytes, err = json.Marshal(rumor)
    if err != nil {
        return shim.Error(err.Error())
    }

    err = stub.PutState(id, rumorAsBytes)
    if err != nil {
        return shim.Error(err.Error())
    }

    return shim.Success(nil)
}

// verifyRumor 验证谣言
func (t *RumorChaincode) verifyRumor(stub shim.ChaincodeStubInterface, args []string) pb.Response {
    if len(args) != 4 {
        return shim.Error("Incorrect number of arguments. Expecting 4")
    }

    id := args[0]
    verifier := args[1]
    isVerified := args[2] == "true"
    comment := args[3]

    // 检查验证权限
    if !t.checkUserPermission(stub, verifier, "VERIFY_RUMOR") {
        return shim.Error("User does not have permission to verify rumor")
    }

    // 获取谣言
    rumorAsBytes, err := stub.GetState(id)
    if err != nil {
        return shim.Error(err.Error())
    }
    if rumorAsBytes == nil {
        return shim.Error("Rumor does not exist")
    }

    // 更新谣言状态
    var rumor Rumor
    err = json.Unmarshal(rumorAsBytes, &rumor)
    if err != nil {
        return shim.Error(err.Error())
    }

    rumor.IsVerified = isVerified
    rumor.Verifier = verifier
    rumor.VerifyTime = stub.GetTxTimestamp().String()
    rumor.VerificationComment = comment

    // 添加验证记录
    historyRecord := HistoryRecord{
        Timestamp: stub.GetTxTimestamp().String(),
        Action:    "VERIFY",
        Operator:  verifier,
        Comment:   comment,
    }
    rumor.History = append(rumor.History, historyRecord)

    // 更新账本
    rumorAsBytes, err = json.Marshal(rumor)
    if err != nil {
        return shim.Error(err.Error())
    }

    err = stub.PutState(id, rumorAsBytes)
    if err != nil {
        return shim.Error(err.Error())
    }

    return shim.Success(nil)
}

// getRumor 获取谣言信息
func (t *RumorChaincode) getRumor(stub shim.ChaincodeStubInterface, args []string) pb.Response {
    if len(args) != 2 {
        return shim.Error("Incorrect number of arguments. Expecting 2")
    }

    id := args[0]
    userId := args[1]

    // 检查查看权限
    if !t.checkUserPermission(stub, userId, "VIEW_RUMOR") {
        return shim.Error("User does not have permission to view rumor")
    }

    rumorAsBytes, err := stub.GetState(id)
    if err != nil {
        return shim.Error(err.Error())
    }
    if rumorAsBytes == nil {
        return shim.Error("Rumor does not exist")
    }

    return shim.Success(rumorAsBytes)
}

// getRumorHistory 获取谣言历史记录
func (t *RumorChaincode) getRumorHistory(stub shim.ChaincodeStubInterface, args []string) pb.Response {
    if len(args) != 2 {
        return shim.Error("Incorrect number of arguments. Expecting 2")
    }

    id := args[0]
    userId := args[1]

    // 检查查看权限
    if !t.checkUserPermission(stub, userId, "VIEW_RUMOR") {
        return shim.Error("User does not have permission to view rumor history")
    }

    rumorAsBytes, err := stub.GetState(id)
    if err != nil {
        return shim.Error(err.Error())
    }
    if rumorAsBytes == nil {
        return shim.Error("Rumor does not exist")
    }

    var rumor Rumor
    err = json.Unmarshal(rumorAsBytes, &rumor)
    if err != nil {
        return shim.Error(err.Error())
    }

    historyAsBytes, err := json.Marshal(rumor.History)
    if err != nil {
        return shim.Error(err.Error())
    }

    return shim.Success(historyAsBytes)
}

func main() {
    err := shim.Start(new(RumorChaincode))
    if err != nil {
        fmt.Printf("Error starting Rumor chaincode: %s", err)
    }
} 