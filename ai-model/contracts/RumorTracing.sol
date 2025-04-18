// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/access/AccessControl.sol";
import "@openzeppelin/contracts/utils/Counters.sol";

contract RumorTracing is AccessControl {
    using Counters for Counters.Counter;
    
    // 角色定义
    bytes32 public constant VERIFIER_ROLE = keccak256("VERIFIER_ROLE");
    bytes32 public constant ADMIN_ROLE = keccak256("ADMIN_ROLE");
    
    // 计数器
    Counters.Counter private _rumorIds;
    Counters.Counter private _verificationIds;
    
    // 谣言结构体
    struct Rumor {
        uint256 id;
        string content;
        string source;
        address creator;
        uint256 timestamp;
        bool isVerified;
        uint256 verificationId;
        string metadata;
    }
    
    // 验证结构体
    struct Verification {
        uint256 id;
        uint256 rumorId;
        address verifier;
        bool isTrue;
        string evidence;
        string comment;
        uint256 timestamp;
    }
    
    // 事件定义
    event RumorCreated(uint256 indexed rumorId, address indexed creator);
    event RumorVerified(uint256 indexed rumorId, uint256 indexed verificationId, address indexed verifier);
    
    // 存储
    mapping(uint256 => Rumor) public rumors;
    mapping(uint256 => Verification) public verifications;
    mapping(uint256 => uint256[]) public rumorVerifications;
    
    constructor() {
        _setupRole(DEFAULT_ADMIN_ROLE, msg.sender);
        _setupRole(ADMIN_ROLE, msg.sender);
    }
    
    // 创建谣言
    function createRumor(
        string memory content,
        string memory source,
        string memory metadata
    ) public returns (uint256) {
        _rumorIds.increment();
        uint256 newRumorId = _rumorIds.current();
        
        rumors[newRumorId] = Rumor({
            id: newRumorId,
            content: content,
            source: source,
            creator: msg.sender,
            timestamp: block.timestamp,
            isVerified: false,
            verificationId: 0,
            metadata: metadata
        });
        
        emit RumorCreated(newRumorId, msg.sender);
        return newRumorId;
    }
    
    // 验证谣言
    function verifyRumor(
        uint256 rumorId,
        bool isTrue,
        string memory evidence,
        string memory comment
    ) public onlyRole(VERIFIER_ROLE) {
        require(rumorId > 0 && rumorId <= _rumorIds.current(), "Invalid rumor ID");
        require(!rumors[rumorId].isVerified, "Rumor already verified");
        
        _verificationIds.increment();
        uint256 newVerificationId = _verificationIds.current();
        
        verifications[newVerificationId] = Verification({
            id: newVerificationId,
            rumorId: rumorId,
            verifier: msg.sender,
            isTrue: isTrue,
            evidence: evidence,
            comment: comment,
            timestamp: block.timestamp
        });
        
        rumors[rumorId].isVerified = true;
        rumors[rumorId].verificationId = newVerificationId;
        rumorVerifications[rumorId].push(newVerificationId);
        
        emit RumorVerified(rumorId, newVerificationId, msg.sender);
    }
    
    // 获取谣言信息
    function getRumor(uint256 rumorId) public view returns (
        uint256 id,
        string memory content,
        string memory source,
        address creator,
        uint256 timestamp,
        bool isVerified,
        uint256 verificationId,
        string memory metadata
    ) {
        require(rumorId > 0 && rumorId <= _rumorIds.current(), "Invalid rumor ID");
        Rumor memory rumor = rumors[rumorId];
        return (
            rumor.id,
            rumor.content,
            rumor.source,
            rumor.creator,
            rumor.timestamp,
            rumor.isVerified,
            rumor.verificationId,
            rumor.metadata
        );
    }
    
    // 获取验证信息
    function getVerification(uint256 verificationId) public view returns (
        uint256 id,
        uint256 rumorId,
        address verifier,
        bool isTrue,
        string memory evidence,
        string memory comment,
        uint256 timestamp
    ) {
        require(verificationId > 0 && verificationId <= _verificationIds.current(), "Invalid verification ID");
        Verification memory verification = verifications[verificationId];
        return (
            verification.id,
            verification.rumorId,
            verification.verifier,
            verification.isTrue,
            verification.evidence,
            verification.comment,
            verification.timestamp
        );
    }
    
    // 获取谣言的验证历史
    function getRumorVerifications(uint256 rumorId) public view returns (uint256[] memory) {
        require(rumorId > 0 && rumorId <= _rumorIds.current(), "Invalid rumor ID");
        return rumorVerifications[rumorId];
    }
    
    // 获取谣言总数
    function getRumorCount() public view returns (uint256) {
        return _rumorIds.current();
    }
    
    // 获取验证总数
    function getVerificationCount() public view returns (uint256) {
        return _verificationIds.current();
    }
    
    // 授予验证者角色
    function grantVerifierRole(address account) public onlyRole(ADMIN_ROLE) {
        grantRole(VERIFIER_ROLE, account);
    }
    
    // 撤销验证者角色
    function revokeVerifierRole(address account) public onlyRole(ADMIN_ROLE) {
        revokeRole(VERIFIER_ROLE, account);
    }
    
    // 检查地址是否为验证者
    function isVerifier(address account) public view returns (bool) {
        return hasRole(VERIFIER_ROLE, account);
    }
} 