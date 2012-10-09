package persistence.metadata.processor.relation;


/*    */ import java.lang.reflect.Field;
/*    */ import javax.persistence.ManyToMany;
/*    */ import javax.persistence.ManyToOne;
/*    */ import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import persistence.annotation.DocumentReferences;
/*    */ 
/*    */ public class RelationMetadataProcessorFactory
/*    */ {
/*    */   public static RelationMetadataProcessor getRelationMetadataProcessor(Field relationField)
/*    */   {
/* 42 */     RelationMetadataProcessor relProcessor = null;
/*    */ 
///* 45 */     if (relationField.isAnnotationPresent(OneToOne.class))
///*    */     {
///* 47 */       relProcessor = new OneToOneRelationMetadataProcessor();
///*    */     }
		// /* 51 */ if (relationField.isAnnotationPresent(OneToMany.class))
		// /* */ {
		// /* 53 */ relProcessor = new OneToManyRelationMetadataProcessor();
		// /* */ }

    if (relationField.isAnnotationPresent(DocumentReferences.class))
     {
       relProcessor = new OneToManyRelationMetadataProcessor();
     }




///* 58 */     else if (relationField.isAnnotationPresent(ManyToOne.class))
///*    */     {
///* 60 */       relProcessor = new ManyToOneRelationMetadataProcessor();
///*    */     }
///* 65 */     else if (relationField.isAnnotationPresent(ManyToMany.class))
///*    */     {
///* 67 */       relProcessor = new ManyToManyRelationMetadataProcessor();
///*    */     }
/*    */ 
/* 71 */     return relProcessor;
/*    */   }
/*    */ }
