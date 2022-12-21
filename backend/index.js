import express from 'express'
import fetch from 'node-fetch'

const app = express()
const port = 3000
const beacons = {"C58C6FC8479A419FA040EE34575CAD04": "57727444edc2cb3880cb7bf6", //Niklas Iphone, Mona Lisa
    "24AB8B4EFD8C4E45AC79DFF20EF814A6":"577271f9edc2cb3880c37dcd", //Linus Iphone, The School of Athens
    "C8232AFA1B79451BAD2ABB716704A8BF":"57726e2fedc2cb3880b61c00" //Toras Ipad, Composition A
};

app.get('/painting', (req, res) => {
    const id = beacons[req.query.id];
    let url = "https://www.wikiart.org/en/api/2/Painting?id=" + id;
    let settings = {method: "Get"};
    fetch(url, settings)
        .then(res => res.json())
        .then((json) => {
            res.send(json)
        });
})

app.get('/', (req, res) => {
    res.send("Hello world!")
})

app.listen(port, () => {
    console.log(`Habitat is up and running at http://localhost:${port}`)
})