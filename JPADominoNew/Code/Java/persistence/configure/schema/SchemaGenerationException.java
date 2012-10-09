package persistence.configure.schema;

/*     */ import persistence.exception.KunderaException;
/*     */ 
/*     */ public class SchemaGenerationException extends KunderaException
/*     */ {
/*     */   private static final long serialVersionUID = 3855497974944993364L;
/*     */   private String dataStoreName;
/*     */   private String schemaName;
/*     */   private String tableName;
/*     */ 
/*     */   public SchemaGenerationException(String arg0, String dataStore, String schema)
/*     */   {
/*  52 */     super(arg0);
/*  53 */     this.dataStoreName = dataStore;
/*  54 */     this.schemaName = schema;
/*     */   }
/*     */ 
/*     */   public SchemaGenerationException(String arg0, String dataStore, String schema, String table)
/*     */   {
/*  89 */     super(arg0);
/*  90 */     this.dataStoreName = dataStore;
/*  91 */     this.schemaName = schema;
/*  92 */     this.tableName = table;
/*     */   }
/*     */ 
/*     */   public SchemaGenerationException(Throwable arg0)
/*     */   {
/* 103 */     super(arg0);
/*     */   }
/*     */ 
/*     */   public SchemaGenerationException(Throwable arg0, String dataStore)
/*     */   {
/* 116 */     super(arg0);
/* 117 */     this.dataStoreName = dataStore;
/*     */   }
/*     */ 
/*     */   public SchemaGenerationException(Throwable arg0, String dataStore, String schema)
/*     */   {
/* 132 */     super(arg0);
/* 133 */     this.dataStoreName = dataStore;
/* 134 */     this.schemaName = schema;
/*     */   }
/*     */ 
/*     */   public SchemaGenerationException(Throwable arg0, String dataStore, String schema, String table)
/*     */   {
/* 151 */     super(arg0);
/* 152 */     this.dataStoreName = dataStore;
/* 153 */     this.schemaName = schema;
/* 154 */     this.tableName = table;
/*     */   }
/*     */ 
/*     */   public SchemaGenerationException(String arg0, Throwable arg1, String dataStore)
/*     */   {
/* 169 */     super(arg0, arg1);
/* 170 */     this.dataStoreName = dataStore;
/*     */   }
/*     */ 
/*     */   public SchemaGenerationException(String arg0, Throwable arg1, String dataStore, String schema, String table)
/*     */   {
/* 189 */     super(arg0, arg1);
/* 190 */     this.dataStoreName = dataStore;
/* 191 */     this.schemaName = schema;
/* 192 */     this.tableName = table;
/*     */   }
/*     */ 
/*     */   public SchemaGenerationException(String arg0, Throwable arg1, String dataStoreName, String databaseName)
/*     */   {
/* 197 */     super(arg0, arg1);
/* 198 */     this.dataStoreName = dataStoreName;
/* 199 */     this.schemaName = databaseName;
/*     */   }
/*     */ }

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.configure.schema.SchemaGenerationException
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */
