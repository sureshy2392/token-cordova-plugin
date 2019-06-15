import TokenSdk

class token: CDVPlugin {
    // global settings
    static let developerKey: String = "4qY7lqQw8NOl9gng0ZHgT4xdiDqxqoGVutuZwrUYQsI"
    static let env: TokenCluster = TokenCluster.sandbox()
    static let realm = "at-bisb"
    static let recoveryAgent = "m:4A6NpTk5XS3GuUEdjMZSTEWpjKD6:5zKtXEAq"
    static let aliasType = Alias_Type.phone
    static let testRealm = "at-clbh"
    
    struct account: Codable {
        var tokenAccountId: String
        var bankAccountNumber : String
        var name: String
        var type: String
    }
    
    struct transfer: Codable {
        var created_at_ms: String
        var description: String
        var amountVal: String
        var amountCurrency: String
        var status: String
    }
    
    struct consent: Codable {
        var tppMemberId: String
        var consentExpiry: String
        var accountList: [String]
    }
    
    
    // create and return a token client object
    
    func getTokenClient() -> TokenClient {
        let builder: TokenClientBuilder?
        builder = TokenClientBuilder()
        builder?.tokenCluster = token.env
        builder?.port = 443
        builder?.useSsl = true
        builder?.developerKey = token.developerKey
        return (builder?.build())!
    }
    
    // create and return an alias object
    
    func makeAliasObject(value: String) -> Alias {
        let alias = Alias()
        alias.type = token.aliasType
        alias.realm = token.realm
        alias.value = value
        return alias
    }
    
    // make common vars
    
    func makeCommonVars() -> (Bool, String, CDVPluginResult, DispatchGroup) {
        let status = Bool()
        let pluginResult = CDVPluginResult()!
        let error = String()
        let dispatchGrp = DispatchGroup()
        
        return (status, error, pluginResult, dispatchGrp)
    }
    
    // create a member and return memberId
    
    @objc(createMember:)
    func createMember(argDict: CDVInvokedUrlCommand) {
        // common vars
        var (status, error, pluginResult, dispatchGrp) = makeCommonVars()
        dispatchGrp.enter()
        
        // read from args
        var args = argDict.arguments![0] as! [String: Any]
        print("received dict arguments", args)
        var mobileNumber: String = args["mobileNumber"] as! String
        
        // method specific vars
        var memberId = String()
        
        // create a member and return member id
        getTokenClient().createMember(makeAliasObject(value: mobileNumber), recoveryAgent: token.recoveryAgent, onSuccess: { TKMember in
            print("createMember:success", TKMember.id)
            memberId = TKMember.id
            dispatchGrp.leave()
        }, onError: { Error in
            print("createMember:failed", Error.localizedDescription)
            error = Error.localizedDescription
            dispatchGrp.leave()
        })
        
        // wait for async and notify main
        dispatchGrp.notify(queue: .main) {
            print("main notified by member: ", memberId)
            if memberId == "" {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: error
                )
            } else {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_OK,
                    messageAs: memberId
                )
            }
            self.commandDelegate!.send(
                pluginResult,
                callbackId: argDict.callbackId
            )
        }
    }
    
    // subscribe for notification and return subscriber id
    
    @objc(subscribe:)
    func subscribe(argDict: CDVInvokedUrlCommand) {
        // common vars
        var (status, error, pluginResult, dispatchGrp) = makeCommonVars()
        dispatchGrp.enter()
        
        // read from args
        var args = argDict.arguments![0] as! [String: Any]
        print("received dict arguments", args)
        var memberId: String = args["memberId"] as! String
        
        // method specific vars
        var subscriberId = String()
        
        // subscribe to token and return subscriber id
        getTokenClient().getMember(memberId, onSuccess: { Member in
            Member.subscribe(toNotifications: token.realm, handlerInstructions: nil, onSuccess: { Subscriber in
                subscriberId = Subscriber.id_p
                print("subscriber id is ", Subscriber.id_p)
                dispatchGrp.leave()
                
            }, onError: { Error in
                print("subcription error ", Error)
                error = Error.localizedDescription
                dispatchGrp.leave()
            })
        }, onError: { Error in
            print("error during subcription ", Error)
            error = Error.localizedDescription
            dispatchGrp.leave()
            
        })
        
        // wait for async and notify main
        dispatchGrp.notify(queue: .main) {
            print("main notified by member: ", memberId)
            if subscriberId == "" {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: error
                )
            } else {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_OK,
                    messageAs: subscriberId
                )
            }
            self.commandDelegate!.send(
                pluginResult,
                callbackId: argDict.callbackId
            )
        }
    }
    
    // pass access token and link accounts
    
    @objc(linkAccounts:)
    func linkAccounts(argDict: CDVInvokedUrlCommand) {
        // common vars
        var (status, error, pluginResult, dispatchGrp) = makeCommonVars()
        dispatchGrp.enter()
        
        // read from args
        var args = argDict.arguments![0] as! [String: Any]
        print("received dict arguments", args)
        var memberId: String = args["memberId"] as! String
        var accessToken: String = args["accessToken"] as! String
        
        accessToken = accessToken + "|" + memberId
        
        getTokenClient().getMember(memberId, onSuccess: { Member in
            Member.linkAccounts(token.testRealm, accessToken: accessToken, onSuccess: { accounts in
                print("accounts linked: ", accounts)
                if accounts != nil {
                    status = true
                    print("linked: ", status)
                    dispatchGrp.leave()
                }
            }, onError: { Error in
                print("error during linking ", Error)
                error = Error.localizedDescription
                dispatchGrp.leave()
            })
        }, onError: { Error in
            print("member lookup error during linking ", Error)
            error = Error.localizedDescription
            dispatchGrp.leave()
        })
        
        // wait for async and notify main
        dispatchGrp.notify(queue: .main) {
            print("main notified by member: ", memberId)
            if status == true {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_OK,
                    messageAs: status
                )
            } else {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: error
                )
            }
            self.commandDelegate!.send(
                pluginResult,
                callbackId: argDict.callbackId
            )
        }
    }
    
    // pass member id to get the list of accounts
    
    @objc(getAccounts:)
    func getAccounts(argDict: CDVInvokedUrlCommand) {
        // common vars
        var (status, error, pluginResult, dispatchGrp) = makeCommonVars()
        dispatchGrp.enter()
        
        // read from args
        var args = argDict.arguments![0] as! [String: Any]
        print("received dict arguments", args)
        var memberId: String = args["memberId"] as! String
        var fetchedList = [Any]()
        let jsonEncoder = JSONEncoder()

        
        getTokenClient().getMember(memberId, onSuccess: { Member in
            Member.getAccounts({ accountList in
                
                for accountElement in accountList {

                var accountStruct = account.init(tokenAccountId: accountElement.id,
                                                 bankAccountNumber: accountElement.accountDetails.identifier,
                                                 name: accountElement.name,
                                                 type: accountElement.accountDetails!.type as! String)
                do {
                    let jsonData = try jsonEncoder.encode(accountStruct)
                    let jsonString = String(data: jsonData, encoding: .utf8)
                    
                    fetchedList.append(jsonString!)
                } catch {
                    
                    }
                }
                
                print("fetched accounts: ", fetchedList)
                status = true
                
                dispatchGrp.leave()
            }, onError: { Error in
                print("error during fetching linked accounts ", Error)
                error = Error.localizedDescription
                dispatchGrp.leave()
            })
        }, onError: { Error in
            print("member lookup error during fetching linked accounts ", Error)
            error = Error.localizedDescription
            dispatchGrp.leave()
        })
        
        // wait for async and notify main
        dispatchGrp.notify(queue: .main) {
            print("main notified by member: ", memberId, status)
            if status == true {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_OK,
                    messageAs: status
                )
            } else {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: error
                )
            }
            self.commandDelegate!.send(
                pluginResult,
                callbackId: argDict.callbackId
            )
        }
    }
    
    @objc(getConsents:)
    func getConsents(argDict: CDVInvokedUrlCommand) {
        // common vars
        var (status, error, pluginResult, dispatchGrp) = makeCommonVars()
        dispatchGrp.enter()
        
        // read from args
        var args = argDict.arguments![0] as! [String: Any]
        print("received dict arguments", args)
        var memberId: String = args["memberId"] as! String
        var fetchedConsents = [String]()
        let jsonEncoder = JSONEncoder()
        
        getTokenClient().getMember(memberId, onSuccess: { member in
            member.getAccessTokensOffset("NULL", limit: 10, onSuccess: { tppResources in
                //print("fetched tppResources: ", tppResources.items)
                
                for resourceElement in tppResources.items {
                    var accountlist: [String] = []
                    let resourceArrayvalue: NSMutableArray = resourceElement.payload!.access.resourcesArray
                    
                    if resourceArrayvalue.count != 0 {
                        for resource in resourceArrayvalue {
                            var temp: AccessBody_Resource = resource as! AccessBody_Resource
                            
                            if temp.account != nil {
                                accountlist.append(temp.account.accountId)
                            }
                            accountlist.removeAll(where: { $0 == "" })
                        }
                       // print("accountlist", accountlist)
                    }
                    
                    var consentStruct = consent.init(
                        tppMemberId: resourceElement.payload.to.id_p,
                        consentExpiry: String(resourceElement.payload!.expiresAtMs),
                        accountList: accountlist
                    )
                    
                    do {
                        let jsonData = try jsonEncoder.encode(consentStruct)
                        let jsonString = String(data: jsonData, encoding: .utf8)
                        
                        fetchedConsents.append(jsonString!)
                    } catch {}
                }
                
                // print("finalTransferList: ", finalTransferList)
                
                status = true
                dispatchGrp.leave()
            }, onError: { Error in
                print("error during fetching consents ", Error)
                error = Error.localizedDescription
                dispatchGrp.leave()
            })
        }, onError: { Error in
            print("member lookup error during fetching consents ", Error)
            error = Error.localizedDescription
            dispatchGrp.leave()
        })
        
        // wait for async and notify main
        dispatchGrp.notify(queue: .main) {
            print("main notified by member: ", memberId, status)
            if status == true {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_OK,
                    messageAs: fetchedConsents
                )
            } else {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: error
                )
            }
            self.commandDelegate!.send(
                pluginResult,
                callbackId: argDict.callbackId
            )
        }
    }
    
    @objc(getTransfers:)
    func getTransfers(argDict: CDVInvokedUrlCommand) {
        // common vars
        var (status, error, pluginResult, dispatchGrp) = makeCommonVars()
        dispatchGrp.enter()
        
        // read from args
        var args = argDict.arguments![0] as! [String: Any]
        print("received dict arguments", args)
        var memberId: String = args["memberId"] as! String
        // var fetchedTransfers = Array<Transfer>()
        var finalTransferList = [String]()
        let jsonEncoder = JSONEncoder()
        
        getTokenClient().getMember(memberId, onSuccess: { member in
            member.getTransfersOffset("NULL", limit: 100, tokenId: nil, onSuccess: { transferlist in
                for transferElement in transferlist.items {
                    var transferStruct = transfer.init(
                        created_at_ms: String(transferElement.createdAtMs),
                        description: transferElement.payload.description_p,
                        amountVal: transferElement.payload.amount.currency,
                        amountCurrency: transferElement.payload.amount.value,
                        status: String(transferElement.status.rawValue)
                    )
                    
                    do {
                        let jsonData = try jsonEncoder.encode(transferStruct)
                        let jsonString = String(data: jsonData, encoding: .utf8)
                        
                        finalTransferList.append(jsonString!)
                    } catch {}
                }
                
                print("finalTransferList: ", finalTransferList)
                status = true
                dispatchGrp.leave()
            }, onError: { Error in
                print("error during fetching transfers ", Error)
                error = Error.localizedDescription
                dispatchGrp.leave()
            })
        }, onError: { Error in
            print("member lookup error during fetching transfers ", Error)
            error = Error.localizedDescription
            dispatchGrp.leave()
        })
        
        // wait for async and notify main
        dispatchGrp.notify(queue: .main) {
            print("main notified by member: ", memberId, status)
            if status == true {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_OK,
                    messageAs: finalTransferList
                )
            } else {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: error
                )
            }
            self.commandDelegate!.send(
                pluginResult,
                callbackId: argDict.callbackId
            )
        }
    }
    
    @objc(getProfile:)
    func getProfile(argDict: CDVInvokedUrlCommand) {
        // common vars
        var (status, error, pluginResult, dispatchGrp) = makeCommonVars()
        dispatchGrp.enter()
        
        // read from args
        var args = argDict.arguments![0] as! [String: Any]
        print("received dict arguments", args)
        var memberId: String = args["memberId"] as! String
        var tppMemberId: String = args["tppMemberId"] as! String
        var tppName = String()
        
        getTokenClient().getMember(memberId, onSuccess: { member in
            member.getProfile(tppMemberId, onSuccess: { profile in
                print("fetched profile: ", profile)
                tppName = profile.displayNameFirst
                status = true
                dispatchGrp.leave()
            }, onError: { Error in
                print("error during fetching profile ", Error)
                error = Error.localizedDescription
                dispatchGrp.leave()
            })
        }, onError: { Error in
            print("member lookup error during fetching profile ", Error)
            error = Error.localizedDescription
            dispatchGrp.leave()
        })
        
        // wait for async and notify main
        dispatchGrp.notify(queue: .main) {
            print("main notified by member: ", memberId, status)
            if status == true {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_OK,
                    messageAs: tppName
                )
            } else {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: error
                )
            }
            self.commandDelegate!.send(
                pluginResult,
                callbackId: argDict.callbackId
            )
        }
    }
    
    @objc(getProfilePicture:)
    func getProfilePicture(argList: CDVInvokedUrlCommand) {
        // local vars
        var status = Bool()
        var pluginResult = CDVPluginResult()
        var error = String()
        var tppPic = Data()
        let dispatchGrp = DispatchGroup()
        dispatchGrp.enter()
        
        // read from args
        
        var argArray = (argList.arguments[0] as AnyObject) as! [Any]
        print("received arguments", argArray)
        
        var memberId: String = argArray[0] as! String
        var tppMemberId: String = (argArray[1] as! String)
        
        getTokenClient().getMember(memberId, onSuccess: { member in
            member.getProfilePicture(tppMemberId, size: ProfilePictureSize(rawValue: 2)!, onSuccess: { profilePic in
                print("fetched profile pic: ", profilePic)
                tppPic = (profilePic?.payload.data_p)!
                status = true
                dispatchGrp.leave()
            }, onError: { Error in
                print("error during fetching profile pic", Error)
                error = Error.localizedDescription
                dispatchGrp.leave()
            })
        }, onError: { Error in
            print("member lookup error during fetching profile pic", Error)
            error = Error.localizedDescription
            dispatchGrp.leave()
        })
        
        // wait for async and notify main
        dispatchGrp.notify(queue: .main) {
            print("main notified by member: ", memberId, status)
            if status == true {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_OK,
                    messageAs: tppPic.base64EncodedString()
                )
            } else {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: error
                )
            }
            self.commandDelegate!.send(
                pluginResult,
                callbackId: argList.callbackId
            )
        }
    }
    
    @objc(unlinkAccounts:)
    func unlinkAccounts(argList: CDVInvokedUrlCommand) {
        // local vars
        var status = Bool()
        var pluginResult = CDVPluginResult()
        var error = String()
        let dispatchGrp = DispatchGroup()
        dispatchGrp.enter()
        
        // read from args
        
        var args = argList.arguments![0] as! [String: Any]
        print("received arguments", args)
        
        print(args["memberId"])
        print((args["accounts"] as! [String]).count)
        
        var memberId: String = ""
        var unlinkAccountlist: String = ""
        
        getTokenClient().getMember(memberId, onSuccess: { member in
            member.unlinkAccounts([unlinkAccountlist], onSuccess: {
                print("unlinked accounts: ")
                status = true
                dispatchGrp.leave()
            }, onError: { Error in
                print("error during unlinking", Error)
                error = Error.localizedDescription
                dispatchGrp.leave()
            })
        }, onError: { Error in
            print("member lookup error during unlink ", Error)
            error = Error.localizedDescription
            dispatchGrp.leave()
        })
        
        // wait for async and notify main
        dispatchGrp.notify(queue: .main) {
            print("main notified by member: ", memberId, status)
            if status == true {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_OK,
                    messageAs: status
                )
            } else {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: error
                )
            }
            self.commandDelegate!.send(
                pluginResult,
                callbackId: argList.callbackId
            )
        }
    }
    
    @objc(deleteMember:)
    func deleteMember(argList: CDVInvokedUrlCommand) {
        // local vars
        var status = Bool()
        var pluginResult = CDVPluginResult()
        var error = String()
        var tppPic = Data()
        let dispatchGrp = DispatchGroup()
        dispatchGrp.enter()
        
        // read from args
        
        var argArray = (argList.arguments[0] as AnyObject) as! [Any]
        print("received arguments", argArray)
        
        var memberId: String = argArray[0] as! String
        getTokenClient().getMember(memberId, onSuccess: { member in
            member.delete({
                print("member deleted")
                status = true
                dispatchGrp.leave()
            }, onError: { Error in
                print("error during delete member", Error)
                error = Error.localizedDescription
                dispatchGrp.leave()
            })
        }, onError: { Error in
            print("member lookup error during member delete", Error)
            error = Error.localizedDescription
            dispatchGrp.leave()
        })
        
        // wait for async and notify main
        dispatchGrp.notify(queue: .main) {
            print("main notified by member: ", memberId, status)
            if status == true {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_OK,
                    messageAs: status
                )
            } else {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: error
                )
            }
            self.commandDelegate!.send(
                pluginResult,
                callbackId: argList.callbackId
            )
        }
    }
}
