package persistence.client;

/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public enum DBType
/*    */ {
/* 26 */   HBASE, MONGODB, CASSANDRA, RDBMS;
/*    */ 
/*    */   static Map<String, DBType> coll;
/*    */ 
/*    */   static
/*    */   {
/* 39 */     coll = new HashMap();
/*    */ 
/* 46 */     coll.put(HBASE.name(), HBASE);
/* 47 */     coll.put(CASSANDRA.name(), CASSANDRA);
/* 48 */     coll.put(MONGODB.name(), MONGODB);
/* 49 */     coll.put(RDBMS.name(), RDBMS);
/*    */   }
/*    */ }

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.client.DBType
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */
