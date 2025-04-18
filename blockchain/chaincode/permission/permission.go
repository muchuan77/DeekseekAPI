package main

import (
    "encoding/json"
    "fmt"
    "github.com/hyperledger/fabric/core/chaincode/shim"
    pb "github.com/hyperledger/fabric/protos/peer"
)

// PermissionChaincode 权限管理链码
type PermissionChaincode struct {
}

// Role 角色结构
type Role struct {
    Name        string   `json:"name"`
    Permissions []string `json:"permissions"`
}

// UserRole 用户角色结构
type UserRole struct {
    UserID string   `json:"userId"`
    Roles  []string `json:"roles"`
}

// Permission 权限结构
type Permission struct {
    Name        string `json:"name"`
    Resource    string `json:"resource"`
    Action      string `json:"action"`
    Description string `json:"description"`
}

// Init 初始化链码
func (t *PermissionChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
    // 初始化默认角色
    adminRole := Role{
        Name: "ADMIN",
        Permissions: []string{"CREATE_RUMOR", "VERIFY_RUMOR", "MANAGE_ROLES", "MANAGE_PERMISSIONS"},
    }
    verifierRole := Role{
        Name: "VERIFIER",
        Permissions: []string{"VERIFY_RUMOR", "VIEW_RUMOR"},
    }
    userRole := Role{
        Name: "USER",
        Permissions: []string{"CREATE_RUMOR", "VIEW_RUMOR"},
    }

    // 存储默认角色
    adminRoleBytes, _ := json.Marshal(adminRole)
    verifierRoleBytes, _ := json.Marshal(verifierRole)
    userRoleBytes, _ := json.Marshal(userRole)

    stub.PutState("ROLE_ADMIN", adminRoleBytes)
    stub.PutState("ROLE_VERIFIER", verifierRoleBytes)
    stub.PutState("ROLE_USER", userRoleBytes)

    return shim.Success(nil)
}

// Invoke 链码调用入口
func (t *PermissionChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
    function, args := stub.GetFunctionAndParameters()

    switch function {
    case "assignRole":
        return t.assignRole(stub, args)
    case "revokeRole":
        return t.revokeRole(stub, args)
    case "checkPermission":
        return t.checkPermission(stub, args)
    case "createRole":
        return t.createRole(stub, args)
    case "updateRole":
        return t.updateRole(stub, args)
    case "getUserRoles":
        return t.getUserRoles(stub, args)
    case "getRolePermissions":
        return t.getRolePermissions(stub, args)
    default:
        return shim.Error("Invalid function name")
    }
}

// assignRole 分配角色
func (t *PermissionChaincode) assignRole(stub shim.ChaincodeStubInterface, args []string) pb.Response {
    if len(args) != 2 {
        return shim.Error("Incorrect number of arguments. Expecting userId and roleName")
    }

    userId := args[0]
    roleName := args[1]

    // 检查角色是否存在
    roleKey := "ROLE_" + roleName
    roleBytes, err := stub.GetState(roleKey)
    if err != nil {
        return shim.Error("Failed to get role")
    }
    if roleBytes == nil {
        return shim.Error("Role does not exist")
    }

    // 获取用户当前角色
    userRoleKey := "USER_ROLE_" + userId
    userRoleBytes, err := stub.GetState(userRoleKey)
    var userRole UserRole

    if userRoleBytes == nil {
        // 新用户
        userRole = UserRole{
            UserID: userId,
            Roles:  []string{roleName},
        }
    } else {
        err = json.Unmarshal(userRoleBytes, &userRole)
        if err != nil {
            return shim.Error("Failed to unmarshal user role")
        }
        // 检查角色是否已分配
        for _, role := range userRole.Roles {
            if role == roleName {
                return shim.Error("Role already assigned to user")
            }
        }
        userRole.Roles = append(userRole.Roles, roleName)
    }

    // 保存更新后的用户角色
    userRoleBytes, err = json.Marshal(userRole)
    if err != nil {
        return shim.Error("Failed to marshal user role")
    }

    err = stub.PutState(userRoleKey, userRoleBytes)
    if err != nil {
        return shim.Error("Failed to save user role")
    }

    return shim.Success(nil)
}

// revokeRole 撤销角色
func (t *PermissionChaincode) revokeRole(stub shim.ChaincodeStubInterface, args []string) pb.Response {
    if len(args) != 2 {
        return shim.Error("Incorrect number of arguments. Expecting userId and roleName")
    }

    userId := args[0]
    roleName := args[1]

    // 获取用户当前角色
    userRoleKey := "USER_ROLE_" + userId
    userRoleBytes, err := stub.GetState(userRoleKey)
    if err != nil {
        return shim.Error("Failed to get user role")
    }
    if userRoleBytes == nil {
        return shim.Error("User has no roles")
    }

    var userRole UserRole
    err = json.Unmarshal(userRoleBytes, &userRole)
    if err != nil {
        return shim.Error("Failed to unmarshal user role")
    }

    // 移除指定角色
    newRoles := make([]string, 0)
    roleFound := false
    for _, role := range userRole.Roles {
        if role != roleName {
            newRoles = append(newRoles, role)
        } else {
            roleFound = true
        }
    }

    if !roleFound {
        return shim.Error("Role not found for user")
    }

    userRole.Roles = newRoles

    // 保存更新后的用户角色
    userRoleBytes, err = json.Marshal(userRole)
    if err != nil {
        return shim.Error("Failed to marshal user role")
    }

    err = stub.PutState(userRoleKey, userRoleBytes)
    if err != nil {
        return shim.Error("Failed to save user role")
    }

    return shim.Success(nil)
}

// checkPermission 检查权限
func (t *PermissionChaincode) checkPermission(stub shim.ChaincodeStubInterface, args []string) pb.Response {
    if len(args) != 2 {
        return shim.Error("Incorrect number of arguments. Expecting userId and permission")
    }

    userId := args[0]
    requiredPermission := args[1]

    // 获取用户角色
    userRoleKey := "USER_ROLE_" + userId
    userRoleBytes, err := stub.GetState(userRoleKey)
    if err != nil {
        return shim.Error("Failed to get user role")
    }
    if userRoleBytes == nil {
        return shim.Success([]byte("false"))
    }

    var userRole UserRole
    err = json.Unmarshal(userRoleBytes, &userRole)
    if err != nil {
        return shim.Error("Failed to unmarshal user role")
    }

    // 检查每个角色的权限
    for _, roleName := range userRole.Roles {
        roleKey := "ROLE_" + roleName
        roleBytes, err := stub.GetState(roleKey)
        if err != nil {
            continue
        }

        var role Role
        err = json.Unmarshal(roleBytes, &role)
        if err != nil {
            continue
        }

        for _, permission := range role.Permissions {
            if permission == requiredPermission {
                return shim.Success([]byte("true"))
            }
        }
    }

    return shim.Success([]byte("false"))
}

// createRole 创建新角色
func (t *PermissionChaincode) createRole(stub shim.ChaincodeStubInterface, args []string) pb.Response {
    if len(args) != 2 {
        return shim.Error("Incorrect number of arguments. Expecting roleName and permissions")
    }

    roleName := args[0]
    var permissions []string
    err := json.Unmarshal([]byte(args[1]), &permissions)
    if err != nil {
        return shim.Error("Failed to unmarshal permissions")
    }

    role := Role{
        Name:        roleName,
        Permissions: permissions,
    }

    roleBytes, err := json.Marshal(role)
    if err != nil {
        return shim.Error("Failed to marshal role")
    }

    roleKey := "ROLE_" + roleName
    err = stub.PutState(roleKey, roleBytes)
    if err != nil {
        return shim.Error("Failed to save role")
    }

    return shim.Success(nil)
}

// updateRole 更新角色权限
func (t *PermissionChaincode) updateRole(stub shim.ChaincodeStubInterface, args []string) pb.Response {
    if len(args) != 2 {
        return shim.Error("Incorrect number of arguments. Expecting roleName and permissions")
    }

    roleName := args[0]
    var permissions []string
    err := json.Unmarshal([]byte(args[1]), &permissions)
    if err != nil {
        return shim.Error("Failed to unmarshal permissions")
    }

    roleKey := "ROLE_" + roleName
    roleBytes, err := stub.GetState(roleKey)
    if err != nil {
        return shim.Error("Failed to get role")
    }
    if roleBytes == nil {
        return shim.Error("Role does not exist")
    }

    var role Role
    err = json.Unmarshal(roleBytes, &role)
    if err != nil {
        return shim.Error("Failed to unmarshal role")
    }

    role.Permissions = permissions

    roleBytes, err = json.Marshal(role)
    if err != nil {
        return shim.Error("Failed to marshal role")
    }

    err = stub.PutState(roleKey, roleBytes)
    if err != nil {
        return shim.Error("Failed to save role")
    }

    return shim.Success(nil)
}

// getUserRoles 获取用户角色
func (t *PermissionChaincode) getUserRoles(stub shim.ChaincodeStubInterface, args []string) pb.Response {
    if len(args) != 1 {
        return shim.Error("Incorrect number of arguments. Expecting userId")
    }

    userId := args[0]
    userRoleKey := "USER_ROLE_" + userId
    userRoleBytes, err := stub.GetState(userRoleKey)
    if err != nil {
        return shim.Error("Failed to get user role")
    }
    if userRoleBytes == nil {
        return shim.Success([]byte("[]"))
    }

    return shim.Success(userRoleBytes)
}

// getRolePermissions 获取角色权限
func (t *PermissionChaincode) getRolePermissions(stub shim.ChaincodeStubInterface, args []string) pb.Response {
    if len(args) != 1 {
        return shim.Error("Incorrect number of arguments. Expecting roleName")
    }

    roleName := args[0]
    roleKey := "ROLE_" + roleName
    roleBytes, err := stub.GetState(roleKey)
    if err != nil {
        return shim.Error("Failed to get role")
    }
    if roleBytes == nil {
        return shim.Error("Role does not exist")
    }

    return shim.Success(roleBytes)
}

func main() {
    err := shim.Start(new(PermissionChaincode))
    if err != nil {
        fmt.Printf("Error starting Permission chaincode: %s", err)
    }
} 