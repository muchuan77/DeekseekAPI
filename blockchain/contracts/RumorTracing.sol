// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts-upgradeable/access/AccessControlUpgradeable.sol";
import "@openzeppelin/contracts-upgradeable/proxy/utils/Initializable.sol";
import "@openzeppelin/contracts-upgradeable/proxy/utils/UUPSUpgradeable.sol";

contract RumorTracing is Initializable, AccessControlUpgradeable, UUPSUpgradeable {
    bytes32 public constant ADMIN_ROLE = keccak256("ADMIN_ROLE");
    bytes32 public constant VERIFIER_ROLE = keccak256("VERIFIER_ROLE");
    
    struct Rumor {
        string content;
        string source;
        string metadata;
        address creator;
        uint256 timestamp;
        bool isVerified;
        string verificationResult;
        address verifier;
        uint256 verificationTimestamp;
    }
    
    struct Verification {
        string result;
        string evidence;
        address verifier;
        uint256 timestamp;
    }
    
    mapping(uint256 => Rumor) public rumors;
    mapping(uint256 => Verification) public verifications;
    mapping(uint256 => uint256[]) public rumorVerifications;
    mapping(address => uint256[]) public userRumors;
    mapping(address => uint256[]) public userVerifications;
    
    uint256 public rumorCount;
    uint256 public verificationCount;
    
    event RumorCreated(uint256 indexed rumorId, address indexed creator, string content);
    event RumorVerified(uint256 indexed rumorId, address indexed verifier, string result);
    event ContractUpgraded(address indexed newImplementation);
    
    function initialize() public initializer {
        __AccessControl_init();
        __UUPSUpgradeable_init();
        
        _setupRole(DEFAULT_ADMIN_ROLE, msg.sender);
        _setupRole(ADMIN_ROLE, msg.sender);
        _setupRole(VERIFIER_ROLE, msg.sender);
    }
    
    function _authorizeUpgrade(address newImplementation) internal override onlyRole(ADMIN_ROLE) {
        emit ContractUpgraded(newImplementation);
    }
    
    function createRumor(string memory _content, string memory _source, string memory _metadata) public returns (uint256) {
        uint256 rumorId = rumorCount++;
        rumors[rumorId] = Rumor({
            content: _content,
            source: _source,
            metadata: _metadata,
            creator: msg.sender,
            timestamp: block.timestamp,
            isVerified: false,
            verificationResult: "",
            verifier: address(0),
            verificationTimestamp: 0
        });
        
        userRumors[msg.sender].push(rumorId);
        emit RumorCreated(rumorId, msg.sender, _content);
        return rumorId;
    }
    
    function verifyRumor(uint256 _rumorId, string memory _result, string memory _evidence) public onlyRole(VERIFIER_ROLE) {
        require(_rumorId < rumorCount, "Rumor does not exist");
        require(!rumors[_rumorId].isVerified, "Rumor already verified");
        
        uint256 verificationId = verificationCount++;
        verifications[verificationId] = Verification({
            result: _result,
            evidence: _evidence,
            verifier: msg.sender,
            timestamp: block.timestamp
        });
        
        rumors[_rumorId].isVerified = true;
        rumors[_rumorId].verificationResult = _result;
        rumors[_rumorId].verifier = msg.sender;
        rumors[_rumorId].verificationTimestamp = block.timestamp;
        
        rumorVerifications[_rumorId].push(verificationId);
        userVerifications[msg.sender].push(verificationId);
        
        emit RumorVerified(_rumorId, msg.sender, _result);
    }
    
    function getRumor(uint256 _rumorId) public view returns (
        string memory content,
        string memory source,
        string memory metadata,
        address creator,
        uint256 timestamp,
        bool isVerified,
        string memory verificationResult,
        address verifier,
        uint256 verificationTimestamp
    ) {
        Rumor memory rumor = rumors[_rumorId];
        return (
            rumor.content,
            rumor.source,
            rumor.metadata,
            rumor.creator,
            rumor.timestamp,
            rumor.isVerified,
            rumor.verificationResult,
            rumor.verifier,
            rumor.verificationTimestamp
        );
    }
    
    function getVerification(uint256 _verificationId) public view returns (
        string memory result,
        string memory evidence,
        address verifier,
        uint256 timestamp
    ) {
        Verification memory verification = verifications[_verificationId];
        return (
            verification.result,
            verification.evidence,
            verification.verifier,
            verification.timestamp
        );
    }
    
    function getRumorVerifications(uint256 _rumorId) public view returns (uint256[] memory) {
        return rumorVerifications[_rumorId];
    }
    
    function getUserRumors(address _user) public view returns (uint256[] memory) {
        return userRumors[_user];
    }
    
    function getUserVerifications(address _user) public view returns (uint256[] memory) {
        return userVerifications[_user];
    }
    
    function getRumorCount() public view returns (uint256) {
        return rumorCount;
    }
    
    function getVerificationCount() public view returns (uint256) {
        return verificationCount;
    }
} 