const express = require('express')
const app = express()
const port = 3000

app.get('/', (req, res) => {
    var id = req.query.id
    res.send('Vad gör du din pissråtta?')
})

app.listen(port, () => {
    console.log(`Habitat is up and running at http://localhost:${port}`)
})