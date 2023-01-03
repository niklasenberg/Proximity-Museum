import express from 'express'
import fetch from 'node-fetch'

const app = express()
const port = 3000
const beacons = {"C58C6FC8479A419FA040EE34575CAD04": "57727444edc2cb3880cb7bf6", //Niklas Iphone, Mona Lisa
    "24AB8B4EFD8C4E45AC79DFF20EF814A6":"577271f9edc2cb3880c37dcd", //Linus Iphone, The School of Athens
    "C8232AFA1B79451BAD2ABB716704A8BF":"57726e2fedc2cb3880b61c00", //Toras Ipad, Composition A
    "4973BCD43AD04793ACA53C3EDB625707":"57726fc1edc2cb3880bbb615", //Ocean
    "0B723EBF867A41649660DDDADB1B655A":"57726e12edc2cb3880b5ea29", //Flower of life
    "A79234343CCA46CFA796F5F5FAE0300C":"57726e1dedc2cb3880b5fd49", //Animals And figures
    "26EF226E9A9E416F9C4944710DEF75D8":"57727c1cedc2cb3880e37b28", //In the forest
    "18AC56008F1440ECADBBEEAEB2FC23BB":"57726e21edc2cb3880b604b8" //Moon light
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

app.get('/artist', (req, res) => {
    let url = "https://www.wikiart.org/en/api/2/PaintingsByArtist?id=" + req.query.id;
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