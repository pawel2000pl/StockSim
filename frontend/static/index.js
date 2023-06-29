const SERVER_PORT = location.port=="8080"?"8081":"8080";
const SERVER_ADRESS = location.protocol+"//"+location.hostname+":"+SERVER_PORT+"/StockSim-1.0-SNAPSHOT/api/";

const Q = (name)=>document.getElementById(name);
const body = Q("main-body");

var user = undefined;

fetch(SERVER_ADRESS+"user/get", {credentials: 'include'}).then(async (request)=>{
    const result = await request.json();
    if (result["id"]) {
        body.innerHTML = await (await fetch("stock.tpl.html")).text();
        user = result;
        initStock(result);
    } else {
        body.innerHTML = await (await fetch("login.tpl.html")).text();
    }
});

const login = async function() {
    const username = Q("login-username").value;
    const password = Q("login-password").value;

    const response = await fetch(SERVER_ADRESS+"user/login", {
        method: "POST",  
        credentials: 'include',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            username: username,
            password: password
        })
    });
    const result = await response.text();
    if (result == "ok")
        window.location = window.location;
    else
        alert("Invalid username or password");
};

const register = async function() {
    const username = Q("register-username").value;
    const password = Q("register-password1").value;
    const passwordConfirm = Q("register-password2").value;
    if (password != passwordConfirm) {
        alert("Passwords are not match to each other");
        return;
    }

    const response = await fetch(SERVER_ADRESS+"user/register", {
        method: "POST",  
        credentials: 'include',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            username: username,
            password: password
        })
    });
    const result = await response.text();
    if (result == "ok") {
        alert("Registred successfully");
        window.location = window.location;
    } else
        alert("Invalid username or password");
};

const logout = async function() {
    await (await fetch(SERVER_ADRESS+"user/logout", {credentials: 'include'})).text();
    window.location = window.location;
};

const makeTable = function(table, list, fieldList) {
    list.forEach(element => {
        const tr = document.createElement("tr");
        tr.data = element;
        fieldList.forEach(record => {
            const td = document.createElement('td');
            const result = (typeof(record)=="function")?record(element):element[record];
            if (typeof(result) == "object")
                for (const [key, value] of Object.entries(result))
                    td[key] = value;
            else
                td.innerText = result;
            tr.appendChild(td);
        });  
        table.appendChild(tr);        
    });
};

const deleteOffer = async function(offer_id) {
    const response = await fetch(SERVER_ADRESS+"offer/delete", {
        method: "DELETE",  
        credentials: 'include',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            id: offer_id
        })
    });
    if (await response.text() == "ok")
        window.location = window.location;
    alert("Error occured. Refresh the page.");
};

const addOffer = async function(copartnershipId, count, price, asSale) {
    const response = await fetch(SERVER_ADRESS+"offer/add", {
        method: "POST",  
        credentials: 'include',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            copartnership: {id: copartnershipId},
            user: {id: user.id},
            price: price,
            count: count,
            saleOffer: asSale
        })
    });
    if (await response.text() == "ok")
        window.location = window.location;
    alert("Error occured. Check input data.");
};

const initStock = async function(user) {
    Q("wallet-value").innerText = user.cash + "$";

    const myWallet = Q('my-wallet');
    myWallet.innerHTML = "";
    makeTable(myWallet, user.wallet, [
        (element)=>element.copartnership.name,
        (element)=>element.count,
        (element)=>element.averPrice + "$",
        (element)=>element.count * element.averPrice + "$",
        (element)=>{return {innerHTML: 
            '<input id="count-of-'+element.id+'" style="width:5em;" type="number" value="0" min="0" max="9999">'+
            '<input id="price-of-'+element.id+'" style="width:5em;" type="number" value="10" min="0" max="99999">'+
            '<button onclick="addOffer('+element.copartnership.id+', Q(\'count-of-'+element.id+'\').value, Q(\'price-of-'+element.id+'\').value, true)">Sale</button>'+
            '<button onclick="addOffer('+element.copartnership.id+', Q(\'count-of-'+element.id+'\').value, Q(\'price-of-'+element.id+'\').value, false)">Purchase</button>'+
            ''
            }}
    ]);
    
    const offerResponse = fetch(SERVER_ADRESS+"offer/mine", {credentials: 'include'});

    const copartnershipsResponse = fetch(SERVER_ADRESS+"copartnership", {credentials: 'include'});
    const copartnershipsList = await(await copartnershipsResponse).json();

    const cpTable = Q("copartnerships-list");
    cpTable.innerHTML = "";
    makeTable(cpTable, copartnershipsList, [
        "name",
        (element)=>element.purchasePrice + "$",
        (element)=>element.salePrice + "$",
        (element)=>element.stockCount,
        (element)=>{return {innerHTML: 
            '<input id="count-of-c-'+element.id+'" style="width:5em;" type="number" value="0" min="0" max="9999">'+
            '<input id="price-of-c-'+element.id+'" style="width:5em;" type="number" value="10" min="0" max="99999">'+
            '<button onclick="addOffer('+element.id+', Q(\'count-of-c-'+element.id+'\').value, Q(\'price-of-c-'+element.id+'\').value, false)">Purchase</button>'+
            ''
        }}
    ]);

    const offersTable = Q("offers-list");
    offersTable.innerHTML = "";
    makeTable(offersTable, await (await offerResponse).json(), [
        (element)=>element.copartnership.name,
        (element)=>element.price+"$",
        "count",
        (element)=>element.price * element.count+"$",
        (element)=>element.isSaleOffer?"Sale":"Purchase",
        (element)=>{return {innerHTML: '<button onclick="deleteOffer('+element.id+')">Delete</button>'}}
    ]);
    

};