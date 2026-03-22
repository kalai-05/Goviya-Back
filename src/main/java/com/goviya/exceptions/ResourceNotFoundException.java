package com.goviya.exceptions;

public class ResourceNotFoundException extends GoviyaException {
    public ResourceNotFoundException(String resource, String id) {
        super(resource + " not found with id: " + id, "RESOURCE_NOT_FOUND");
    }
}
