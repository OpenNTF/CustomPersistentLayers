package persistence.context;


/*     */ import persistence.graph.Node;
/*     */ import persistence.graph.NodeLink;
import persistence.metadata.model.Relation;
/*     */ import persistence.metadata.model.Relation.ForeignKey;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
import java.util.Set;
/*     */ 
/*     */ public class FlushStackManager
/*     */ {
/*     */   public void buildFlushStack(PersistenceCache pc)
/*     */   {
/*  37 */     MainCache mainCache = (MainCache)pc.getMainCache();
/*     */ 
/*  39 */     Set headNodes = mainCache.getHeadNodes();
/*     */ 
/*  41 */     for (Object obj : headNodes)
/*     */     {
	Node headNode=(Node)obj;
/*  43 */       addNodesToFlushStack(pc, headNode);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addNodesToFlushStack(PersistenceCache pc, Node node)
/*     */   {
/*  57 */     FlushStack flushStack = pc.getFlushStack();
/*  58 */     MainCache mainCache = (MainCache)pc.getMainCache();
/*     */ 
/*  60 */     Map children = node.getChildren();
/*     */ 
/*  64 */     if (children != null)
/*     */     {
/*  66 */       Map oneToOneChildren = new HashMap();
/*  67 */       Map oneToManyChildren = new HashMap();
/*  68 */       Map manyToOneChildren = new HashMap();
/*  69 */       Map manyToManyChildren = new HashMap();
/*     */ 
/*  71 */       for (Object obj: children.keySet())
/*     */       {
	NodeLink nodeLink =(NodeLink)obj;
/*  73 */         Relation.ForeignKey multiplicity = nodeLink.getMultiplicity();
/*     */ 
/*  75 */         switch (multiplicity.ordinal())
/*     */         {
/*     */         case 1:
/*  78 */           oneToOneChildren.put(nodeLink, children.get(nodeLink));
/*  79 */           break;
/*     */         case 2:
/*  81 */           oneToManyChildren.put(nodeLink, children.get(nodeLink));
/*  82 */           break;
/*     */         case 3:
/*  84 */           manyToOneChildren.put(nodeLink, children.get(nodeLink));
/*  85 */           break;
/*     */         case 4:
/*  87 */           manyToManyChildren.put(nodeLink, children.get(nodeLink));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  94 */       for (Object obj : oneToManyChildren.keySet())
/*     */       {
	NodeLink nodeLink=(NodeLink)obj;
/*  97 */         Node childNode = mainCache.getNodeFromCache(nodeLink.getTargetNodeId());
/*     */ 
/*  99 */         if (!(childNode.isTraversed()))
/*     */         {
/* 101 */           addNodesToFlushStack(pc, childNode);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 107 */       for (Object obj : manyToManyChildren.keySet())
/*     */       {
	NodeLink nodeLink=(NodeLink)obj;
/* 110 */         Node childNode = mainCache.getNodeFromCache(nodeLink.getTargetNodeId());
/* 111 */         if (!(childNode.isTraversed()))
/*     */         {
/* 113 */           addNodesToFlushStack(pc, childNode);
/*     */         }
/*     */       }
/*     */ 
/* 117 */       for (Object obj : oneToOneChildren.keySet())
/*     */       {
	NodeLink nodeLink=(NodeLink)obj;
/* 119 */         if (!(node.isTraversed()))
/*     */         {
/* 122 */           node.setTraversed(true);
/* 123 */           flushStack.push(node);
/*     */ 
/* 126 */           Node childNode = mainCache.getNodeFromCache(nodeLink.getTargetNodeId());
/* 127 */           addNodesToFlushStack(pc, childNode);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 132 */       for (Object obj : manyToOneChildren.keySet())
/*     */       {
	NodeLink nodeLink=(NodeLink)obj;
/* 134 */         if (!(node.isTraversed()))
/*     */         {
/* 137 */           node.setTraversed(true);
/* 138 */           flushStack.push(node);
/*     */         }
/*     */ 
/* 142 */         Node childNode = mainCache.getNodeFromCache(nodeLink.getTargetNodeId());
/*     */ 
/* 146 */         Map parents = childNode.getParents();
/* 147 */         for (Object obj1: parents.keySet())
/*     */         {
	NodeLink parentLink =(NodeLink)obj1;
/* 149 */           Relation.ForeignKey multiplicity = parentLink.getMultiplicity();
/* 150 */           if (multiplicity.equals(Relation.ForeignKey.MANY_TO_ONE))
/*     */           {
/* 152 */             Node parentNode = (Node)parents.get(parentLink);
/*     */ 
/* 154 */             if (!(parentNode.isTraversed()))
/*     */             {
/* 156 */               addNodesToFlushStack(pc, parentNode);
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 162 */         if (!(childNode.isTraversed()))
/*     */         {
/* 164 */           addNodesToFlushStack(pc, childNode);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 172 */     if (node.isTraversed())
/*     */       return;
/* 174 */     node.setTraversed(true);
/* 175 */     flushStack.push(node);
/*     */   }
/*     */ 
/*     */   public void clearFlushStack(PersistenceCache pc)
/*     */   {
/* 188 */     FlushStack flushStack = pc.getFlushStack();
/*     */ 
/* 190 */     if ((flushStack == null) || (flushStack.isEmpty()))
/*     */       return;
/* 192 */     flushStack.clear();
/*     */   }
/*     */ }

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.persistence.context.FlushStackManager
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */
