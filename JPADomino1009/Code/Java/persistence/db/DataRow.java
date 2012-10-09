package persistence.db;

/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class DataRow<TF>
/*     */ {
/*     */   private String id;
/*     */   private String columnFamilyName;
/*     */   private List<TF> columns;
/*     */ 
/*     */   public DataRow()
/*     */   {
/*  45 */     this.columns = new ArrayList();
/*     */   }
/*     */ 
/*     */   public DataRow(String id, String columnFamilyName, List<TF> columns)
/*     */   {
/*  60 */     this.id = id;
/*  61 */     this.columnFamilyName = columnFamilyName;
/*  62 */     this.columns = columns;
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  72 */     return this.id;
/*     */   }
/*     */ 
/*     */   public void setId(String id)
/*     */   {
/*  83 */     this.id = id;
/*     */   }
/*     */ 
/*     */   public String getColumnFamilyName()
/*     */   {
/*  93 */     return this.columnFamilyName;
/*     */   }
/*     */ 
/*     */   public void setColumnFamilyName(String columnFamilyName)
/*     */   {
/* 104 */     this.columnFamilyName = columnFamilyName;
/*     */   }
/*     */ 
/*     */   public List<TF> getColumns()
/*     */   {
/* 114 */     return this.columns;
/*     */   }
/*     */ 
/*     */   public void setColumns(List<TF> columns)
/*     */   {
/* 125 */     this.columns = columns;
/*     */   }
/*     */ 
/*     */   public void addColumn(TF column)
/*     */   {
/* 136 */     this.columns.add(column);
/*     */   }
/*     */ }

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.db.DataRow
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */
