package persistence.metadata.model;


import persistence.event.CallbackMethod; /*     */
import java.lang.reflect.Field; /*     */
import java.lang.reflect.Method; /*     */
import java.util.ArrayList; /*     */
import java.util.HashMap; /*     */
import java.util.List; /*     */
import java.util.Map; /*     */
import java.util.Map.Entry;

import javax.persistence.Column; /*     */
import javax.persistence.JoinColumn;
import javax.persistence.PrimaryKeyJoinColumn;

/*     */
/*     */public final class EntityMetadata
/*     */{
	/*     */Class<?> entityClazz;
	/*     */private String tableName;
	/*     */private String schema;
	/*     */private String persistenceUnit;
	/*     */private String indexName;
	/* 60 */private boolean isIndexable = true;
	/*     */
	/* 63 */private boolean cacheable = false;
	/*     */private Method readIdentifierMethod;
	/*     */private Method writeIdentifierMethod;
	/* 79 */private Map<String, Column> columnsMap = new HashMap();

	/* 91 */private Map<Class, List> callbackMethodsMap = new HashMap();
	/* 98 */private Map<String, Relation> relationsMap = new HashMap();
	/*     */private List<String> relationNames;
	/*     */private boolean isParent;

	// DOMINO SPECIFIC
	private String dbName;
	private String formName;
	private String viewName;

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	/*     */
	/*     */public EntityMetadata(Class<?> entityClazz)
	/*     */{
		/* 213 */this.entityClazz = entityClazz;
		/*     */}

	/*     */
	/*     */public Class<?> getEntityClazz()
	/*     */{
		/* 224 */return this.entityClazz;
		/*     */}

	/*     */
	/*     */public String getTableName()
	/*     */{
		/* 234 */return this.tableName;
		/*     */}

	/*     */
	/*     */public void setTableName(String tableName)
	/*     */{
		/* 245 */this.tableName = tableName;
		/*     */}

	/*     */
	/*     */public String getSchema()
	/*     */{
		/* 255 */return this.schema;
		/*     */}

	/*     */
	/*     */public void setSchema(String schema)
	/*     */{
		/* 266 */this.schema = schema;
		/*     */}

	/*     */
	/*     */public String getPersistenceUnit()
	/*     */{
		/* 276 */return this.persistenceUnit;
		/*     */}

	/*     */
	/*     */public void setPersistenceUnit(String persistenceUnit)
	/*     */{
		/* 287 */this.persistenceUnit = persistenceUnit;
		/*     */}

	/*     */
	/*     */public Method getReadIdentifierMethod()
	/*     */{
		/* 318 */return this.readIdentifierMethod;
		/*     */}

	/*     */
	/*     */public void setReadIdentifierMethod(Method readIdentifierMethod)
	/*     */{
		/* 329 */this.readIdentifierMethod = readIdentifierMethod;
		/*     */}

	/*     */
	/*     */public Method getWriteIdentifierMethod()
	/*     */{
		/* 339 */return this.writeIdentifierMethod;
		/*     */}

	/*     */
	/*     */public void setWriteIdentifierMethod(Method writeIdentifierMethod)
	/*     */{
		/* 350 */this.writeIdentifierMethod = writeIdentifierMethod;
		/*     */}

	/*     */
	/*     */public Map<String, Column> getColumnsMap()
	/*     */{
		/* 360 */return this.columnsMap;
		/*     */}

	/*     */

	/*     */
	/*     */public Column getColumn(String key)
	/*     */{
		/* 383 */return ((Column) this.columnsMap.get(key));
		/*     */}

	/*     */

	/*     */
	/*     */public List<Column> getColumnsAsList()
	/*     */{
		/* 406 */return new ArrayList(this.columnsMap.values());
		/*     */}

	/*     */

	/*     */
	/*     */public List<String> getColumnFieldNames()
	/*     */{
		/* 426 */return new ArrayList(this.columnsMap.keySet());
		/*     */}

	/*     */

	/*     */public void addColumn(String key, Column column)
	/*     */{
		/* 449 */this.columnsMap.put(key, column);
		/*     */}

	/*     */

	/*     */
	/*     */public void addRelation(String property, Relation relation)
	/*     */{
		/* 486 */this.relationsMap.put(property, relation);
		/* 487 */addRelationName(relation);
		/*     */}

	/*     */
	/*     */public Relation getRelation(String property)
	/*     */{
		/* 499 */return ((Relation) this.relationsMap.get(property));
		/*     */}

	/*     */
	/*     */public List<Relation> getRelations()
	/*     */{
		/* 509 */return new ArrayList(this.relationsMap.values());
		/*     */}

	/*     */

	/*     */
	/*     */public String getIndexName()
	/*     */{
		/* 529 */return this.indexName;
		/*     */}

	/*     */
	/*     */public void setIndexName(String indexName)
	/*     */{
		/* 540 */this.indexName = indexName;
		/*     */}

	/*     */
	/*     */public boolean isIndexable()
	/*     */{
		/* 550 */return this.isIndexable;
		/*     */}

	/*     */
	/*     */public void setIndexable(boolean isIndexable)
	/*     */{
		/* 561 */this.isIndexable = isIndexable;
		/*     */}

	/*     */

	/*     */
	/*     */public Map<Class, List> getCallbackMethodsMap()
	/*     */{
		/* 582 */return this.callbackMethodsMap;
		/*     */}

	public void setCallbackMethodsMap(Map<Class, List> callbackMethodsMap) {
		this.callbackMethodsMap = callbackMethodsMap;
	}

	/*     */
	/*     */public List<? extends CallbackMethod> getCallbackMethods(Class<?> event)
	/*     */{
		/* 595 */return ((List) this.callbackMethodsMap.get(event));
		/*     */}

	/*     */

	/*     */
	/*     */public boolean isCacheable()
	/*     */{
		/* 631 */return this.cacheable;
		/*     */}

	/*     */
	/*     */public void setCacheable(boolean cacheable)
	/*     */{
		/* 642 */this.cacheable = cacheable;
		/*     */}

	/*     */
	// /* */ public String toString()
	// /* */ {
	// /* 653 */ int start = 0;
	// /* 654 */ StringBuilder builder = new StringBuilder();
	// /* 655 */ builder.append(new
	// StringBuilder().append(this.entityClazz.getName()).append(" (\n").toString());
	// /* 656 */ builder.append(new
	// StringBuilder().append("\tTable: ").append(this.tableName).append(", \n").toString());
	// /* 657 */ builder.append(new
	// StringBuilder().append("\tKeyspace: ").append(this.schema).append(",\n").toString());
	// /* 658 */ builder.append(new
	// StringBuilder().append("\tPersistence Unit: ").append(this.persistenceUnit).append(",\n").toString());
	// /* 660 */ builder.append(new
	// StringBuilder().append("\tReadIdMethod: ").append(this.readIdentifierMethod.getName()).append(",\n").toString());
	// /* 661 */ builder.append(new
	// StringBuilder().append("\tWriteIdMethod: ").append(this.writeIdentifierMethod.getName()).append(",\n").toString());
	// /* 662 */ builder.append(new
	// StringBuilder().append("\tCacheable: ").append(this.cacheable).append(",\n").toString());
	// /* */
	// /* 664 */ if (!(this.columnsMap.isEmpty()))
	// /* */ {
	// /* 666 */ builder.append("\tColumns (");
	// /* 667 */ for (Column col : this.columnsMap.values())
	// /* */ {
	// /* 669 */ if (start++ != 0)
	// /* */ {
	// /* 671 */ builder.append(", ");
	// /* */ }
	// /* 673 */ builder.append(col.name());
	// /* */ }
	// /* 675 */ builder.append("),\n");
	// /* */ }
	// /* */
	//
	//
	// /* */
	// /* 717 */ if (!(this.callbackMethodsMap.isEmpty()))
	// /* */ {
	// /* 719 */ builder.append("\tListeners (\n");
	// /* 720 */ for (Map.Entry entry : this.callbackMethodsMap.entrySet())
	// /* */ {
	// /* 722 */ String key = ((Class)entry.getKey()).getSimpleName();
	// /* 723 */ for (Object obj : (List)entry.getValue())
	// /* */ {
	// CallbackMethod cbm=(CallbackMethod)obj;
	// /* 725 */ builder.append(new
	// StringBuilder().append("\t\t").append(key).append(": ").append(cbm).append("\n").toString());
	// /* */ }
	// /* */ }
	// /* */ String key;
	// /* 728 */ builder.append("\t)\n");
	// /* */ }
	// /* */
	// /* 731 */ if (!(this.relationsMap.isEmpty()))
	// /* */ {
	// /* 733 */ builder.append("\tRelation (\n");
	// /* 734 */ for (Relation rel : this.relationsMap.values())
	// /* */ {
	// /* 736 */ builder.append(new
	// StringBuilder().append("\t\t").append(rel.getTargetEntity().getName()).append("#").append(rel.getProperty().getName()).toString());
	// /* 737 */ builder.append(new
	// StringBuilder().append(" (").append(rel.getCascades()).toString());
	// /* 738 */ builder.append(new
	// StringBuilder().append(", ").append(rel.getType()).toString());
	// /* 739 */ builder.append(new
	// StringBuilder().append(", ").append(rel.fetchType).toString());
	// /* 740 */ builder.append(")\n");
	// /* */ }
	// /* 742 */ builder.append("\t)\n");
	// /* */ }
	// /* */
	// /* 745 */ builder.append(")");
	// /* 746 */ return builder.toString();
	// /* */ }
	/*     */

	/*     */
	/*     */public boolean isParent()
	/*     */{
		/* 785 */return this.isParent;
		/*     */}

	/*     */
	/*     */public void setParent(boolean isParent)
	/*     */{
		/* 794 */this.isParent = isParent;
		/*     */}

	/*     */

	/*     */
	/*     */public List<String> getRelationNames()
	/*     */{
		/* 810 */return this.relationNames;
		/*     */}

	/*     */
	/*     */private void addRelationName(Relation rField)
	/*     */{
		/* 821 */if (rField.isRelatedViaJoinTable())
			/*     */return;
		/* 823 */String relationName = getJoinColumnName(rField.getProperty());
		/* 824 */if (rField.getProperty().isAnnotationPresent(
				PrimaryKeyJoinColumn.class))
		/*     */{
			/* 826 */// relationName = getIdColumn().getName();
			/*     */}
		/*     */
		/* 829 */addToRelationNameCollection(relationName);
		/*     */}

	/*     */
	/*     */private void addToRelationNameCollection(String relationName)
	/*     */{
		/* 841 */if (this.relationNames == null)
		/*     */{
			/* 843 */this.relationNames = new ArrayList();
			/*     */}
		/* 845 */if (relationName == null)
			/*     */return;
		/* 847 */this.relationNames.add(relationName);
		/*     */}

	/*     */
	/*     */private String getJoinColumnName(Field relation)
	/*     */{
		/* 860 */String columnName = null;
		/* 861 */JoinColumn ann = (JoinColumn) relation
				.getAnnotation(JoinColumn.class);
		/* 862 */if (ann != null)
		/*     */{
			/* 864 */columnName = ann.name();
			/*     */}
		/*     */
		/* 867 */return ((columnName != null) ? columnName : relation.getName());
		/*     */}

	/*     */
	/*     */public static enum Type
	/*     */{
		/* 117 */COLUMN_FAMILY {
			public boolean isColumnFamilyMetadata() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isDocumentMetadata() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isSuperColumnFamilyMetadata() {
				// TODO Auto-generated method stub
				return false;
			}
		},
		SUPER_COLUMN_FAMILY {
			public boolean isColumnFamilyMetadata() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isDocumentMetadata() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isSuperColumnFamilyMetadata() {
				// TODO Auto-generated method stub
				return false;
			}
		};

	}
}
