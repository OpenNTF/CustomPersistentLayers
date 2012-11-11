package persistence.context;


/*    */ import persistence.graph.Node;
/*    */ import java.util.Stack;
/*    */ 
/*    */ public class FlushStack extends Stack<Node>
/*    */ {
/*    */   public String toString()
/*    */   {
/* 37 */     StringBuffer sb = new StringBuffer();
			 sb.append("Flush Stack size of :"+this.size());
/* 38 */     sb.append(" (From top to bottom):\n");
/* 39 */     sb.append("--------------------------------------------\n");
/* 40 */     for (int i = this.elementCount - 1; i >= 0; --i)
/*    */     {
/* 42 */       sb.append("|").append(get(i)).append("\t|\n");
/*    */     }
/* 44 */     sb.append("--------------------------------------------");
/* 45 */     return sb.toString();
/*    */   }
/*    */ }

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.persistence.context.FlushStack
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */
