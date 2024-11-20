async function logJsonData() {
    const response = await fetch("http://localhost:8080/api/gathers/1/detail");
    const jsonData = await response.json();
    console.log(jsonData);
    return jsonData;
}
