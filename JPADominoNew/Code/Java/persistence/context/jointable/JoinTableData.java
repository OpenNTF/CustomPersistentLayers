package persistence.context.jointable;


/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class JoinTableData
/*     */ {
/*     */   private String joinTableName;
/*     */   private Class<?> entityClass;
/*     */   private String joinColumnName;
/*     */   private String inverseJoinColumnName;
/*     */   private OPERATION operation;
/*     */   Map<Object, Set<Object>> joinTableRecords;
/*     */ 
/*     */   public JoinTableData(OPERATION operation, String joinTableName, String joinColumnName, String inverseJoinColumnName, Class<?> entityClass)
/*     */   {
/*  59 */     this.operation = operation;
/*  60 */     this.joinTableName = joinTableName;
/*  61 */     this.joinColumnName = joinColumnName;
/*  62 */     this.inverseJoinColumnName = inverseJoinColumnName;
/*  63 */     this.entityClass = entityClass;
/*     */ 
/*  65 */     this.joinTableRecords = new HashMap();
/*     */   }
/*     */ 
/*     */   public String getJoinTableName()
/*     */   {
/*  73 */     return this.joinTableName;
/*     */   }
/*     */ 
/*     */   public void setJoinTableName(String joinTableName)
/*     */   {
/*  82 */     this.joinTableName = joinTableName;
/*     */   }
/*     */ 
/*     */   public String getJoinColumnName()
/*     */   {
/*  90 */     return this.joinColumnName;
/*     */   }
/*     */ 
/*     */   public void setJoinColumnName(String joinColumnName)
/*     */   {
/*  99 */     this.joinColumnName = joinColumnName;
/*     */   }
/*     */ 
/*     */   public String getInverseJoinColumnName()
/*     */   {
/* 107 */     return this.inverseJoinColumnName;
/*     */   }
/*     */ 
/*     */   public void setInverseJoinColumnName(String inverseJoinColumnName)
/*     */   {
/* 116 */     this.inverseJoinColumnName = inverseJoinColumnName;
/*     */   }
/*     */ 
/*     */   public Map<Object, Set<Object>> getJoinTableRecords()
/*     */   {
/* 124 */     return this.joinTableRecords;
/*     */   }
/*     */ 
/*     */   public Class<?> getEntityClass()
/*     */   {
/* 132 */     return this.entityClass;
/*     */   }
/*     */ 
/*     */   public void setEntityClass(Class<?> entityClass)
/*     */   {
/* 141 */     this.entityClass = entityClass;
/*     */   }
/*     */ 
/*     */   public OPERATION getOperation()
/*     */   {
/* 149 */     return this.operation;
/*     */   }
/*     */ 
/*     */   public void setOperation(OPERATION operation)
/*     */   {
/* 158 */     this.operation = operation;
/*     */   }
/*     */ 
/*     */   public void addJoinTableRecord(Object key, Set<Object> values)
/*     */   {
/* 167 */     Set existingValues = (Set)this.joinTableRecords.get(key);
/* 168 */     if (existingValues == null)
/*     */     {
/* 170 */       existingValues = new HashSet();
/* 171 */       existingValues.addAll(values);
/* 172 */       this.joinTableRecords.put(key, existingValues);
/*     */     }
/*     */     else
/*     */     {
/* 176 */       existingValues.addAll(values);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum OPERATION
/*     */   {
/*  38 */     INSERT, UPDATE, DELETE;
/*     */   }
/*     */ }

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.persistence.context.jointable.JoinTableData
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */
