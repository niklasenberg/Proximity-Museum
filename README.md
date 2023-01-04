
# Context aware museum application 

Our project aims to create a smartphone application that utilizes proximity sensing to provide context-aware information about museum artifacts to visitors during exhibitions. 
As the visitor approaches an artwork, 
the application will display relevant details about the piece,
 such as the artist, year of creation, and origin. 
 Our vision is for the application to personalize and 
 enhance the efficiency of the museum visit for the visitors.
## Authors

- [@Torawh](https://github.com/Torawh)
- [@niklasenberg](https://www.github.com/niklasenberg)
- [@Linusvall](https://www.github.com/Linusvall)



## Screenshots of UI

![App Screenshot](https://user-images.githubusercontent.com/119488859/210562569-081b8137-f873-48da-9349-646acdb6ac38.png)
![App Screenshot](https://user-images.githubusercontent.com/119488859/210562592-4bf2ee73-d664-4778-a7d8-c10114177508.png)
![App Screenshot](https://user-images.githubusercontent.com/119488859/210562610-9180ad0f-3e23-42f7-a159-6bf1a6b69d66.png)


## API Reference 

#### Get painting

```http
  GET /painting
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `beacon_id` | `string` | **Required**. Beacon id. |

#### Get paintings by artist

```http
  GET /artist
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `artist_id`      | `string` | **Required**. Id of artist to fetch |


## Color Reference

| Color             | Hex                                                                |
| ----------------- | ------------------------------------------------------------------ |
| Header | ![#BE9A7B](https://via.placeholder.com/10/BE9A7B?text=+) #BE9A7B |
| Background | ![#E9DDD2](https://via.placeholder.com/10/E9DDD2?text=+) #E9DDD2 |
