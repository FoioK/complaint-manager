# Complaint Management Microservice Documentation

## Overview

This microservice provides a RESTful API for managing customer complaints. It allows for creating, updating, and retrieving complaints, with features such as pagination, sorting, and filtering.

## Swagger UI

The API documentation is available through Swagger UI, which provides an interactive interface to explore and test the API endpoints.

**Access Swagger UI:** `http://localhost:8080/swagger-ui.html`

Through Swagger UI, you can:
- View all available endpoints
- See detailed request and response schemas
- Test the API directly from your browser
- View additional API metadata

To use Swagger UI:
1. Navigate to the Swagger UI URL in your web browser
2. Explore the available endpoints listed in the interface
3. Click on an endpoint to expand its details
4. Use the "Try it out" button to send requests and view responses in real-time

Note: Ensure the application is running locally or replace `localhost:8080` with the appropriate host and port where your service is deployed.


## API Endpoints

### 1. Add a New Complaint

**Endpoint:** `POST /api/complaints`

**Request Body:**
```json
{
  "productId": 123,
  "content": "The product arrived damaged.",
  "complainant": "John Doe"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "productId": 123,
  "content": "The product arrived damaged.",
  "creationDate": "2024-09-07T10:30:00Z",
  "complainant": "John Doe",
  "country": "US",
  "claimCounter": 1
}
```

**Note:** The `country` field is automatically populated based on the requester's IP address.

### 2. Get All Complaints

**Endpoint:** `GET /api/complaints`

**Query Parameters:**
- `productId` (optional): Filter by product ID
- `complainant` (optional): Filter by complainant name (case-insensitive, partial match)
- `page` (optional, default: 0): Page number for pagination
- `size` (optional, default: 20): Number of items per page

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "productId": 123,
      "content": "The product arrived damaged.",
      "creationDate": "2024-09-07T10:30:00Z",
      "complainant": "John Doe",
      "country": "US",
      "claimCounter": 1
    },
    // ... more complaints
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "pageNumber": 0,
    "pageSize": 20,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 5,
  "totalElements": 100,
  "last": false,
  "size": 20,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 20,
  "first": true,
  "empty": false
}
```

### 3. Update Complaint Content

**Endpoint:** `PUT /api/complaints/{id}`

**Path Parameters:**
- `id`: The ID of the complaint to update

**Request Body:**
```json
{
  "content": "Updated complaint content."
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "productId": 123,
  "content": "Updated complaint content.",
  "creationDate": "2024-09-07T10:30:00Z",
  "complainant": "John Doe",
  "country": "US",
  "claimCounter": 1
}
```

### 4. Get Complaint by ID

**Endpoint:** `GET /api/complaints/{id}`

**Path Parameters:**
- `id`: The ID of the complaint to retrieve

**Response:** `200 OK`
```json
{
  "id": 1,
  "productId": 123,
  "content": "The product arrived damaged.",
  "creationDate": "2024-09-07T10:30:00Z",
  "complainant": "John Doe",
  "country": "US",
  "claimCounter": 1
}
```

## Usage Examples

### Adding a New Complaint

```bash
curl -X POST http://localhost:8080/api/complaints \
  -H "Content-Type: application/json" \
  -d '{"productId": 123, "content": "The product arrived damaged.", "complainant": "John Doe"}'
```

### Retrieving Complaints with Filtering and Sorting

```bash
curl "http://localhost:8080/api/complaints?productId=123&complainant=John&page=0&size=20&sortBy=creationDate&sortDirection=desc"
```

This request will:
- Filter complaints for product ID 123
- Filter complainants whose names contain "John" (case-insensitive)
- Return the first page of results with 20 items per page
- Sort the results by creation date in descending order

### Updating a Complaint

```bash
curl -X PUT http://localhost:8080/api/complaints/1 \
  -H "Content-Type: application/json" \
  -d '{"content": "Updated complaint content."}'
```

## Error Handling

The API uses standard HTTP status codes to indicate the success or failure of requests:

- `200 OK`: The request was successful
- `201 Created`: A new resource was successfully created
- `400 Bad Request`: The request was invalid or cannot be served
- `404 Not Found`: The requested resource does not exist
- `500 Internal Server Error`: The server encountered an unexpected condition

Error responses will include a message describing the error.

## Notes

- The `country` field is automatically populated based on the requester's IP address using a geolocation service.
- Complaints are unique per product ID and complainant. If a duplicate complaint is submitted, the `claimCounter` will be incremented instead of creating a new complaint.
- All dates are returned in ISO 8601 format (UTC).

For any additional information or support, please contact the development team.
