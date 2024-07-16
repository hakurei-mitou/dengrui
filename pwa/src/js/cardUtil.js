import axios from "axios"

/**
 * local backend database
 */
if (window.indexedDB) {
    console.log("支持 IndexedDB");
} else {
    alert("不支持 IndexedDB ！");
}

function getDatetime() {
    let datetime = new Date().toISOString();
    return datetime
}


function getStore(db, storeName) {
    // 创建事务，并指定对象存储空间和读写权限
    let transaction = db.transaction([storeName], 'readwrite');
    // 获取对象存储空间
    let store = transaction.objectStore(storeName);
    return store
}

// let gitHubInfo = {
//     user: 'hakurei-mitou',
//     repo: 'dengrui-test',
//     accessToken: '',
//     dataDir: 'cardData-pwa-test-2'
// }

let gitHubInfo = {
    user: null,
    repo: null,
    accessToken: null,
    dataDir: 'cardData'
}


async function getFileFromGitHub(filepath) {
    if (filepath[0] != '/') {
        filepath = '/' + filepath
    }
    let url = `https://raw.githubusercontent.com/${gitHubInfo['user']}/${gitHubInfo['repo']}/main${filepath}`;
    let resp = await axios.get(url)
    return resp.data
}

async function getFilenameListFromGitHub(dirpath, infoData = null) {
    if (dirpath[0] != '/') {
        dirpath = '/' + dirpath
    }

    let apiUrl = ""
    let token = ""
    if (infoData == null) {
        apiUrl = `https://api.github.com/repos/${gitHubInfo['user']}/${gitHubInfo['repo']}/contents${dirpath}`;
        token = gitHubInfo['accessToken']
    } else {
        apiUrl = `https://api.github.com/repos/${infoData['user']}/${infoData['repo']}/contents${dirpath}`;
        token = infoData['accessToken']
    }

    try {
        let resp = await axios.get(apiUrl, {
            headers: {
                Authorization: `token ${token}`
            }
        })

        let files = resp.data;
        let fileNames = files.map(file => file.name);

        return fileNames
    } catch (e) {
        // console.log("exception catched!");
        return null
    }
}

function uploadJsonString2GitHub(jsonString, filename) {

    const apiUrl = `https://api.github.com/repos/${gitHubInfo['user']}/${gitHubInfo['repo']}/contents/${gitHubInfo['dataDir']}/${filename}`;

    // 将字符串内容转换为 Base64 格式（GitHub API 要求）
    // 使用 TextEncoder 将字符串转换为 Uint8Array
    const encoder = new TextEncoder();
    const fileContentBytes = encoder.encode(jsonString);
    // 使用 btoa 将 Uint8Array 转换为 Base64 编码
    const contentBase64 = btoa(String.fromCharCode.apply(null, fileContentBytes));

    // 准备请求参数
    const data = {
        message: 'Create new file via API', // 提交信息
        content: contentBase64, // 文件内容的 Base64 编码
        branch: 'main' // 分支名称，通常是主分支
    };

    axios.put(apiUrl, data, {
        headers: {
            Authorization: `token ${gitHubInfo['accessToken']}`
        }
    })
}

function getLatestFileName(names) {
    let latestName = names.reduce((maxDate, currentDate) => {
        return currentDate > maxDate ? currentDate : maxDate;
    }, names[0]);
    return latestName
}


function updateDBFromGitHub(db, latestName) {
    console.log("updateDBFromGitHub");
    // get json from GitHub
    getFileFromGitHub(gitHubInfo['dataDir'] + '/' + latestName).then(jsonfile => {
        // console.log("jsonfile = ", jsonfile);
        let cardData = JSON.parse(jsonfile)
        // console.log("cardData = ", cardData);
        
        // set datetime
        let datetime = latestName.replace('.json', '')
        let infoStore = getStore(db, 'info') 
        infoStore.put(datetime, 'updateDatetime')

        // transfer json data into DB
        let cardStore = getStore(db, 'card')
        let maxIncrementCardId = 0
        cardData.forEach(card => {
            cardStore.put(card)
            if (card.id > maxIncrementCardId) {
                maxIncrementCardId = card.id
            }
        })
        infoStore.put(maxIncrementCardId, 'maxIncrementCardId')

    })
}

function updateGitHubFromDB(db) {
    console.log("updateGitHubFromDB");
    let cardStore = getStore(db, 'card')
    let request = cardStore.getAll();
    request.onsuccess = function(event) {
        let cardData = event.target.result
        let dataJsonString = JSON.stringify(cardData)
        // console.log("dataJsonString = ", dataJsonString)

        // set datetime
        let datetime = getDatetime()
        let infoStore = getStore(db, 'info') 
        infoStore.put(datetime, 'updateDatetime')

        let filename = datetime + '.json'
        uploadJsonString2GitHub(dataJsonString, filename)
    }
}




function setTestData() {
    // for test
    let cardsObject = [
        {
            id: 1,
            type: 0,
            content: {
                title: "",
                translationTitle: "",
                originalText: "type:0000000000",
                translationText: "",
                author: "111",
                translator: "",
                book: "",
                edition: "",
                note: ""
            }
        },
        {
            id: 2,
            type: 1,
            content: {
                title: "",
                translationTitle: "",
                originalText: "type:111111111111",
                translationText: "",
                lyric: "",
                voice: "",
                compose: "",
                arrangement: "",
                album: "",
                source: "",
                note: ""
            }
        },
        {
            id: 3,
            type: 2,
            content: {
                originalText: "type:22222222222",
                translation_text: "",
                sayer: "",
                translator: "",
                source: "",
                note: ""
            }
        }
    ]

    let cardStore = getStore(db, 'card');
    cardStore.add(cardsObject[0]);
    cardStore.add(cardsObject[1]);
    cardStore.add(cardsObject[2]);

    let infoStore = getStore(db, 'info');
    infoStore.put(3, 'maxIncrementCardId')

    // continuly add card: 1, 2, 3
    infoStore.put([1, 2, 3,], 'history')
}



// create DB and Object Store
let dbRequest = indexedDB.open('dengruiDB')

dbRequest.onupgradeneeded = function(event) {
    // this funciton only occured at the DB created 
    console.log("onupgrade");
    var db = event.target.result

    // A store is like a table.
    let cardStore = db.createObjectStore('card', { keyPath: 'id'})
    // infoStore is only locally stored.
    let infoStore = db.createObjectStore('info')

    // init data
    infoStore.add('0000-00-00T00:00:00.000Z', 'updateDatetime')
    infoStore.add(0, 'maxIncrementCardId')
    // history will ignore histoyr type
    // max history cardId number = 1000
    infoStore.add([], 'history')
    infoStore.add(null, 'latestReadInAllCardId')
    infoStore.add(gitHubInfo, 'gitHubInfo')
};


function syncGitHubAndDBData(db) {
    // get GitHub info
    let infoStore = getStore(db, 'info')
    // in this time, the onsuccess of openning DB may be not occured, so need to request gitHubInfo here.
    let request = infoStore.get('gitHubInfo');
    request.onsuccess = function(event) {
        gitHubInfo = request.result

        // get latest filename
        getFilenameListFromGitHub(gitHubInfo['dataDir']).then(names => {
            console.log("names = ", names);

            let latestName = null
            if (names == null) {
                latestName = null
            } else {
                latestName = getLatestFileName(names)
            }
            console.log("latestName = ", latestName);

            // compare updatetime between DB and GitHub
            let infoStore = getStore(db, 'info')
            let request = infoStore.get('updateDatetime')
            request.onsuccess = function(event) {
                let updateDatetime = request.result
                console.log("updateDatetime = ", updateDatetime);
                let gitHubDatetime = latestName.replace('.json', '')
                if (updateDatetime == gitHubDatetime) {
                    console.log("不需同步");
                    return
                } else if (updateDatetime < gitHubDatetime) {
                    updateDBFromGitHub(db, latestName)
                } else {
                    updateGitHubFromDB(db)
                }
            }

        })
    }
}


let db = null

dbRequest.onsuccess = (event) => {
    console.log("onsuccess");
    db = event.target.result

    let infoStore = getStore(db, 'info')
    let request = infoStore.get('gitHubInfo')
    request.onsuccess = (e) => {
        let info = e.target.result
        // console.log("info = ", info);
        gitHubInfo = info
    }

    // setTestData()
}

dbRequest.onerror = (event) => {
    console.alert("打开数据库失败，程序终止");
}


function getRandomElement(array) {
    if (array.length < 1) {
        return null
    }
    // 生成一个随机索引，范围是 [0, array.length - 1]
    let randomIndex = Math.floor(Math.random() * array.length)
    return array[randomIndex]
}

/**
 * methods for calling backend
 */

export const cardUtil = {

    historyType: {
        READ: 0,
        ADD: 1,
        UPDATE: 2,
        DELETE: 3,
        READINALL: 4
    },

    NoCardId: null,

    async getCard(id) {
        return new Promise((resolve, reject) => {
            let cardStore = getStore(db, 'card');
            let request = cardStore.get(id);
            request.onsuccess = () => {
                let data = request.result;
                // console.log("card data = ", data);
                resolve(data); // 将数据通过 resolve 返回
            };
        });
    },

    async getLatestCardIds(latestNumber = 1) {
        return new Promise((resolve, reject) => {
            let cardStore = getStore(db, 'info');
            let request = cardStore.get('history');
            request.onsuccess = () => {
                let historyCardIds = request.result;
                historyCardIds = historyCardIds.slice(-latestNumber)
                console.log("history = ", historyCardIds);
                resolve(historyCardIds); // 将数据通过 resolve 返回
            };
        });
    },

    async getNewCardId() {
        return new Promise((resolve, reject) => {
            let cardStore = getStore(db, 'card');
            let request = cardStore.getAllKeys();
            request.onsuccess = () => {
                let allKeys = request.result;
                console.log("allKeys = ", allKeys);
                let randomId = getRandomElement(allKeys)
                resolve(randomId); // 将数据通过 resolve 返回
            };
        });
    },

    async pushHistory(cardId, type=cardUtil.historyType.READ) {
        console.log("pushHistory, cardId = ", cardId, "type = ", type);
        if (cardId < 1) {
            console.log("cardId error, cardId = ", cardId);
            return false
        }
            
        let cardStore = getStore(db, 'info');
        if (type == this.historyType.READINALL) {
            cardStore.put(cardId, 'latestReadInAllCardId')
        } else {
            let request = cardStore.get('history');
            request.onsuccess = () => {
                let historyCardIds = request.result;
                historyCardIds.push(cardId)
                // max number = 1000
                historyCardIds = historyCardIds.slice(-1000)
                cardStore.put(historyCardIds, 'history')
            };
        }
    },

    async submitAddCardData(cardData) {
        console.log("addCard, cardData = ", cardData);
        let infoStore = getStore(db, 'info');
        let request = infoStore.get('maxIncrementCardId')
        request.onsuccess = () => {
            let maxIncrementCardId = request.result
            // newID is the return ID after add card operation
            let newId = maxIncrementCardId + 1
            cardData.id = newId
            infoStore.put(newId, 'maxIncrementCardId')
            let cardStore = getStore(db, 'card');
            cardStore.add(cardData)
            
            this.pushHistory(newId, this.historyType.ADD)
        }
    },
    
    async submitUpdateCardData(cardData) {
        console.log("updateCard, cardData = ", cardData)
        let cardStore = getStore(db, 'card')
        cardStore.put(cardData)
        this.pushHistory(cardData.id, this.historyType.UPDATE)
        return null
    },

    async getAllCardIds() {
        return new Promise((resolve, reject) => {
            let cardStore = getStore(db, 'card');
            let request = cardStore.getAllKeys();
            request.onsuccess = () => {
                let allKeys = request.result;
                // console.log("allKeys = ", allKeys);
                resolve(allKeys); // 将数据通过 resolve 返回
            };
        });
    },

    async getLatestReadInAllCardId() {
        return new Promise((resolve, reject) => {
            let cardStore = getStore(db, 'info');
            let request = cardStore.get('latestReadInAllCardId');
            request.onsuccess = () => {
                let latestReadInAllCardId = request.result;
                this.getAllCardIds().then(cardIds => {
                    if (latestReadInAllCardId == null) {
                        if (cardIds.length < 1) {
                            resolve(null)
                        } else {
                            resolve(cardIds[0])
                        }
                    } else {
                        // if be deleted:
                        while (latestReadInAllCardId != null && latestReadInAllCardId > 0) {
                            if (cardIds.includes(latestReadInAllCardId)) {
                                resolve(latestReadInAllCardId)
                                return
                            } else {
                                latestReadInAllCardId = latestReadInAllCardId - 1
                            }
                        }
                    }
                })
            };
        });
    },

    async searchCard(keyword) {
        return new Promise((resolve, reject) => {
            let cardStore = getStore(db, 'card')
            let cardIds = []
            cardStore.getAll().onsuccess = event => {
                let allCards = event.target.result
                // console.log("allCards = ", allCards);
                // 处理每一个 card
                for (let card of allCards) {
                    Object.entries(card.content).forEach(function ([key, value]) {
                        // console.log('card id = ' + card.id + ' : ' + key + '->' + value);
                        if (value.includes(keyword)) {
                            cardIds.push(card.id)
                        }
                    });
                }
                // unique:
                cardIds = [...new Set(cardIds)];
                console.log("cardIds= ", cardIds);
                resolve(cardIds)
            }
        })

    },

    async searchCardField(cardTypeName, cardFieldName, keyword) {
        // console.log(cardTypeName +'  '+ cardFieldName);
        return new Promise((resolve, reject) => {
            let cardStore = getStore(db, 'card')
            cardStore.getAll().onsuccess = event => {
                let fieldData = []
                let allCards = event.target.result
                // console.log("allCards = ", allCards);
                for (let card of allCards) {
                    let type = card.type
                    let content = card.content
                    if (cardTypeName == 'literature' && type == 0) {
                        if (content[cardFieldName].includes(keyword)) {
                            fieldData.push(content[cardFieldName])
                        }
                    } else if (cardTypeName == 'song' && type == 1) {
                        if (content[cardFieldName].includes(keyword)) {
                            fieldData.push(content[cardFieldName])
                        }
                    } else if (cardTypeName == 'words' && type == 2) {
                        if (content[cardFieldName].includes(keyword)) {
                            fieldData.push(content[cardFieldName])
                        }
                    } 
                }

                // unique:
                fieldData = [...new Set(fieldData)];
                fieldData = fieldData.slice(-7)
                resolve(fieldData)
            }
        })
    },


    async latestCardField(cardTypeName, cardFieldName, number=5) {
        // console.log(cardTypeName +'  '+ cardFieldName);
        return new Promise((resolve, reject) => {
            let cardStore = getStore(db, 'card')
            cardStore.getAll().onsuccess = event => {
                let fieldData = []
                let allCards = event.target.result
                // console.log("allCards = ", allCards);
                for (let i = allCards.length - 1; i >= 0; i--) {
                    let type = allCards[i].type
                    let content = allCards[i].content
                    if (cardTypeName == 'literature' && type == 0) {
                        if (content[cardFieldName].length > 0) {
                            fieldData.push(content[cardFieldName])
                        }
                    } else if (cardTypeName == 'song' && type == 1) {
                        if (content[cardFieldName].length > 0) {
                            fieldData.push(content[cardFieldName])
                        }
                    } else if (cardTypeName == 'words' && type == 2) {
                        if (content[cardFieldName].length > 0) {
                            fieldData.push(content[cardFieldName])
                        }
                    } 
                }
                // unique:
                fieldData = [...new Set(fieldData)];
                fieldData = fieldData.slice(-5)
                resolve(fieldData)
            }
        })
    },

    async deleteCard(cardId) {
        // deleteCard has not history
        if (cardId < 1) {
            console.log("error cardId ! cardId = ", cardId);
            return false
        }
        let cardStore = getStore(db, 'card');
        cardStore.delete(cardId);
    },

    async tryConnectGitHub(infoData = null) {
        let names = null
        if (infoData == null) {
            names = await getFilenameListFromGitHub('/')
            console.log("names = ", names);
            if (names != null) {
                console.log("connect GitHub success!");
                return true
            } else {
                alert("Cannot connect to GitHub! \n Add, Update, and Delete Operation has been disabled! \n Please check network or set the GitHub info!")
                return false
            }
        } else {
            names = await getFilenameListFromGitHub('/', infoData)
            if (names != null) {
                return true
            } else {
                return false
            }
        }

    },

    async syncData() {
        try {
            syncGitHubAndDBData()
        } catch(e) {
            alert("Sync error! \n Please check network or set the GitHub info!");
        }
    },

    async setGitHubInfo(infoData) {
        // infoData:
        // username
        // repoName
        // accessToken
        // change attributes's names:
        let info = {
            user: infoData.username,
            repo: infoData.repoName,
            accessToken: infoData.accessToken
        }

        // connect GitHub to check infoData
        this.tryConnectGitHub(info).then(resp => {
            if (resp) {
                let infoStore = getStore(db, 'info')
                let data = {
                    user: "",
                    repo: "",
                    accessToken: "",
                }
                Object.assign(data, info)
                let request = infoStore.put(data, 'gitHubInfo')
                request.onsuccess = (e) => {
                    console.log("set info success!");
                }
            } else {
                alert("The info is not valid!")
            }
        })
    },
    
    closeApplication() {
        db.close()
        // close the tab page
        window.close()
    },

    dbOpened() {
        return db == null ? false : true
    }

}

// for debug
// indexedDB.deleteDatabase("dengruiDB")
