import express from 'express'
import fetch from 'node-fetch'

const app = express()
const port = 3000
const beacons = {"monalisa": "57727444edc2cb3880cb7bf6"};

app.get('/painting', (req, res) => {
    const id = beacons[req.query.id];
    let url = "https://www.wikiart.org/en/api/2/Painting?id=" + id;
    let settings = {method: "Get"};
    fetch(url, settings)
        .then(res => res.json())
        .then((json) => {
            res.json(json)
        });
})

app.get('/', (req, res) => {
    res.send("Hello world!")
})

app.listen(port, () => {
    console.log(`Habitat is up and running at http://localhost:${port}`)
})