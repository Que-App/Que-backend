package app.services.exceptions

class EntityNotFoundException(entityName: String) : Exception("No such $entityName was found")