IndexedDB 是一种现代浏览器支持的客户端存储数据的技术，它提供了一种机制来存储结构化数据，并支持事务操作。以下是使用 IndexedDB 的基本步骤：

### 1. 打开或创建数据库

首先，我们需要打开或创建一个 IndexedDB 数据库，并指定数据库的名称和版本号。如果数据库不存在，则会被创建；如果存在但版本号较低，则会触发升级逻辑。

```javascript
let db;
let request = indexedDB.open('myDatabase', 1);

// 数据库打开或升级时的回调函数
request.onupgradeneeded = function(event) {
    db = event.target.result;
    
    // 创建一个对象存储空间（类似于表）
    let store = db.createObjectStore('storeName', { keyPath: 'id' });
};

// 数据库打开成功时的回调函数
request.onsuccess = function(event) {
    db = event.target.result;
    console.log('数据库打开成功');
};

// 错误处理
request.onerror = function(event) {
    console.error('打开数据库失败');
};
```

### 2. 创建对象存储空间（Object Store）

在 `onupgradeneeded` 事件处理函数中，我们可以创建一个对象存储空间（Object Store），类似于关系型数据库中的表。在创建时，需要指定一个主键（keyPath）来唯一标识存储的对象。

```javascript
let request = indexedDB.open('myDatabase', 1);

request.onupgradeneeded = function(event) {
    db = event.target.result;
    
    // 创建对象存储空间
    let store = db.createObjectStore('storeName', { keyPath: 'id' });
};
```

### 3. 存储数据

一旦数据库和对象存储空间准备就绪，就可以通过事务来存储数据到 IndexedDB 中。

```javascript
// 创建事务，并指定存储空间和读写权限
let transaction = db.transaction(['storeName'], 'readwrite');
let store = transaction.objectStore('storeName');

// 添加数据
store.add({ id: 1, name: 'John Doe', age: 30 });

// 在事务完成后的回调函数中处理结果
transaction.oncomplete = function(event) {
    console.log('数据存储成功');
};

transaction.onerror = function(event) {
    console.error('数据存储失败');
};
```

### 4. 检索数据

从 IndexedDB 中检索数据也需要通过事务进行操作。

```javascript
// 创建只读事务
let transaction = db.transaction(['storeName'], 'readonly');
let store = transaction.objectStore('storeName');

// 根据主键获取单个对象
let getRequest = store.get(1);

// 处理获取到的数据
getRequest.onsuccess = function(event) {
    let data = event.target.result;
    console.log('获取的数据：', data);
};

getRequest.onerror = function(event) {
    console.error('获取数据失败');
};
```

### 5. 更新和删除数据

更新和删除数据也是通过事务进行操作，类似于添加数据的方式。

```javascript
// 创建读写事务
let transaction = db.transaction(['storeName'], 'readwrite');
let store = transaction.objectStore('storeName');

// 更新数据
let updateRequest = store.put({ id: 1, name: 'Updated Name', age: 35 });

updateRequest.onsuccess = function(event) {
    console.log('数据更新成功');
};

updateRequest.onerror = function(event) {
    console.error('数据更新失败');
};

// 删除数据
let deleteRequest = store.delete(1);

deleteRequest.onsuccess = function(event) {
    console.log('数据删除成功');
};

deleteRequest.onerror = function(event) {
    console.error('数据删除失败');
};
```

### 6. 关闭数据库连接

最后，在所有操作完成后，应当关闭数据库连接以释放资源。

```javascript
db.close();
```

以上是基本的 IndexedDB 使用方法。通过事务管理数据的读写操作，并且利用对象存储空间来存储和检索数据。IndexedDB 提供了强大的功能，能够在客户端持久化存储大量结构化数据，适用于需要离线操作或本地数据存储的应用场景。