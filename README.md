# Cache Management Service

This service provides APIs for managing entities in the cache, supporting actions like adding, removing, and fetching entities from the cache and the database.

## Overview

This service exposes several REST endpoints for performing cache operations such as adding, removing, and fetching entities by their ID, along with clearing the cache.

## Endpoints

### 1. **Add Entity to Cache**

- **URL:** `/api/v1/cache`
- **Method:** `POST`
- **Request Body:**
    - The request body should contain the entity to be added to the cache, represented by the `Data` object.
  
  Example Request:
  ```json
  {
      "id": 1,
      "name": "Sample Entity"
  }
