package persistence.metadata.processor.relation;

import persistence.metadata.model.EntityMetadata;
import java.lang.reflect.Field;

public abstract interface RelationMetadataProcessor
{
  public abstract void addRelationIntoMetadata(Field paramField, EntityMetadata paramEntityMetadata);
}
