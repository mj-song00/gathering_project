
async function findGathersLonJsonData(latitude,longitude){

    const distance = document.getElementById("distance").value;
    const response = await fetch("http://localhost:8080/api/kakaoMap/RedisSearch", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({latitude ,longitude ,distance })
    });
    const jsonData = await response.json();
    console.log(jsonData);
    return jsonData;
}

async function getAddressLonJsonData(){
    const address = document.getElementById("address").value;
    const response = await fetch("http://localhost:8080/api/kakaoMap", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({address})
    });
    const jsonData = await response.json();
    console.log(jsonData);
    return jsonData;
}