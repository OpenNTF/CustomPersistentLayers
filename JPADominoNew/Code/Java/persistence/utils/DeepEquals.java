package persistence.utils;


/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.SortedSet;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ public class DeepEquals
/*     */ {
/*  32 */   private static final Map<Class, Boolean> _customEquals = new ConcurrentHashMap();
/*     */ 
/*  34 */   private static final Map<Class, Boolean> _customHash = new ConcurrentHashMap();
/*     */ 
/*  36 */   private static final Map<Class, Collection<Field>> _reflectedFields = new ConcurrentHashMap();
/*     */ 
/*     */   public static boolean deepEquals(Object a, Object b)
/*     */   {
/*  80 */     Set visited = new HashSet();
/*  81 */     return deepEquals(a, b, visited);
/*     */   }
/*     */ 
/*     */   private static boolean deepEquals(Object a, Object b, Set visited)
/*     */   {
/*  86 */     LinkedList stack = new LinkedList();
/*  87 */     stack.addFirst(new DualKey(a, b));
/*     */     DualKey dualKey;
/*  89 */     while (!(stack.isEmpty()))
/*     */     {
/*  91 */       dualKey = (DualKey)stack.removeFirst();
/*  92 */       visited.add(dualKey);
/*     */ 
/*  94 */       if ((dualKey._key1 == null) || (dualKey._key2 == null))
/*     */       {
/*  96 */         if (dualKey._key1 != dualKey._key2);
/*  98 */         return false;
/*     */       }
/*     */ 
/* 103 */       if (!(dualKey._key1.getClass().equals(dualKey._key2.getClass())))
/*     */       {
/* 105 */         return false;
/*     */       }
/*     */ 
/* 113 */       if (dualKey._key1.getClass().isArray())
/*     */       {
/* 115 */         int len = Array.getLength(dualKey._key1);
/* 116 */         if (len != Array.getLength(dualKey._key2))
/*     */         {
/* 118 */           return false;
/*     */         }
/*     */ 
/* 121 */         for (int i = 0; i < len; ++i)
/*     */         {
/* 123 */           DualKey dk = new DualKey(Array.get(dualKey._key1, i), Array.get(dualKey._key2, i));
/* 124 */           if (visited.contains(dk))
/*     */             continue;
/* 126 */           stack.addFirst(dk);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 135 */       if (dualKey._key1 instanceof SortedSet)
/*     */       {
/* 137 */         if (!(compareOrdered(dualKey, stack, visited)));
/* 139 */         return false;
/*     */       }
/*     */ 
/* 148 */       if (dualKey._key1 instanceof Set)
/*     */       {
/* 150 */         if (!(compareUnordered((Set)dualKey._key1, (Set)dualKey._key2, visited)));
/* 152 */         return false;
/*     */       }
/*     */ 
/* 161 */       if (dualKey._key1 instanceof Collection)
/*     */       {
/* 163 */         if (!(compareOrdered(dualKey, stack, visited)));
/* 165 */         return false;
/*     */       }
/*     */ 
/* 173 */       if (dualKey._key1 instanceof SortedMap)
/*     */       {
/* 175 */         Map map1 = (Map)dualKey._key1;
/* 176 */         Map map2 = (Map)dualKey._key2;
/*     */ 
/* 178 */         if (map1.size() != map2.size())
/*     */         {
/* 180 */           return false;
/*     */         }
/*     */ 
/* 183 */         Iterator i1 = map1.entrySet().iterator();
/* 184 */         Iterator i2 = map2.entrySet().iterator();
/*     */         while (true) {
/* 186 */           if (i1.hasNext());
/* 188 */           Map.Entry entry1 = (Map.Entry)i1.next();
/* 189 */           Map.Entry entry2 = (Map.Entry)i2.next();
/*     */ 
/* 191 */           DualKey dk = new DualKey(entry1.getKey(), entry2.getKey());
/* 192 */           if (!(visited.contains(dk)))
/*     */           {
/* 194 */             stack.addFirst(dk);
/*     */           }
/*     */ 
/* 197 */           dk = new DualKey(entry1.getValue(), entry2.getValue());
/* 198 */           if (!(visited.contains(dk)))
/*     */           {
/* 200 */             stack.addFirst(dk);
/*     */           }
/*     */         }
/*     */       }
/*     */       Map map2;
/* 208 */       if (dualKey._key1 instanceof Map)
/*     */       {
/* 210 */         Map map1 = (Map)dualKey._key1;
/* 211 */         map2 = (Map)dualKey._key2;
/*     */ 
/* 213 */         if (map1.size() != map2.size())
/*     */         {
/* 215 */           return false;
/*     */         }
/*     */ 
/* 218 */         for (Object obj : map1.entrySet())
/*     */         {
	Map.Entry entry1=(Map.Entry)obj;
/* 220 */           Map.Entry saveEntry2 = null;
/* 221 */           for (Object obj1 : map2.entrySet())
/*     */           {
	Map.Entry entry2=(Map.Entry)obj1;
/* 225 */             if (deepEquals(entry1.getKey(), entry2.getKey(), visited))
/*     */             {
/* 227 */               saveEntry2 = entry2;
/* 228 */               break;
/*     */             }
/*     */           }
/*     */ 
/* 232 */           if (saveEntry2 == null)
/*     */           {
/* 234 */             return false;
/*     */           }
/*     */ 
/* 238 */           DualKey dk = new DualKey(entry1.getValue(), saveEntry2.getValue());
/* 239 */           if (!(visited.contains(dk)))
/*     */           {
/* 241 */             stack.addFirst(dk);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 248 */       if (hasCustomEquals(dualKey._key1.getClass()))
/*     */       {
/* 250 */         if (!(dualKey._key1.equals(dualKey._key2)));
/* 252 */         return false;
/*     */       }
/*     */ 
/* 257 */       Collection<Field> fields = getDeepDeclaredFields(dualKey._key1.getClass());
/*     */ 
/* 259 */       for (Field field : fields)
/*     */       {
/*     */         try
/*     */         {
/* 263 */           DualKey dk = new DualKey(field.get(dualKey._key1), field.get(dualKey._key2));
/* 264 */           if (!(visited.contains(dk)))
/*     */           {
/* 266 */             stack.addFirst(dk);
/*     */           }
/*     */         }
/*     */         catch (Exception e) {
/*     */         }
/* 271 */         continue;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 276 */     return true;
/*     */   }
/*     */ 
/*     */   private static boolean compareOrdered(DualKey dualKey, LinkedList<DualKey> stack, Collection visited)
/*     */   {
/* 293 */     Collection col1 = (Collection)dualKey._key1;
/* 294 */     Collection col2 = (Collection)dualKey._key2;
/* 295 */     if (col1.size() != col2.size())
/*     */     {
/* 297 */       return false;
/*     */     }
/*     */ 
/* 300 */     Iterator i1 = col1.iterator();
/* 301 */     Iterator i2 = col2.iterator();
/*     */ 
/* 303 */     while (i1.hasNext())
/*     */     {
/* 305 */       DualKey dk = new DualKey(i1.next(), i2.next());
/* 306 */       if (!(visited.contains(dk)))
/*     */       {
/* 308 */         stack.addFirst(dk);
/*     */       }
/*     */     }
/* 311 */     return true;
/*     */   }
/*     */ 
/*     */   private static boolean compareUnordered(Collection col1, Collection col2, Set visited)
/*     */   {
/* 330 */     if (col1.size() != col2.size())
/*     */     {
/* 332 */       return false;
/*     */     }
/*     */ 
/* 335 */     int h1 = deepHashCode(col1);
/* 336 */     int h2 = deepHashCode(col2);
/* 337 */     if (h1 != h2)
/*     */     {
/* 340 */       return false;
/*     */     }
/*     */ 
/* 343 */     List copy = new ArrayList(col2);
/*     */ 
/* 345 */     for (Iterator i$ = col1.iterator(); i$.hasNext(); ) { Object element1 = i$.next();
/*     */ 
/* 347 */       int len = copy.size();
/* 348 */       for (int i = 0; i < len; ++i)
/*     */       {
/* 352 */         if (!(deepEquals(element1, copy.get(i), visited)))
/*     */           continue;
/* 354 */         copy.remove(i);
/* 355 */         break;
/*     */       }
/*     */ 
/* 359 */       if (len == copy.size())
/*     */       {
/* 361 */         return false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 366 */     return true;
/*     */   }
/*     */ 
/*     */   public static boolean hasCustomEquals(Class c)
/*     */   {
/* 371 */     Class origClass = c;
/* 372 */     if (_customEquals.containsKey(c))
/*     */     {
/* 374 */       return ((Boolean)_customEquals.get(c)).booleanValue();
/*     */     }
/*     */ 
/* 377 */     while (!(Object.class.equals(c)))
/*     */     {
/*     */       try
/*     */       {
/* 381 */         c.getDeclaredMethod("equals", new Class[] { Object.class });
/* 382 */         _customEquals.put(origClass, Boolean.valueOf(true));
/* 383 */         return true;
/*     */       }
/*     */       catch (Exception ignored)
/*     */       {
/*     */       }
/* 388 */       c = c.getSuperclass();
/*     */     }
/* 390 */     _customEquals.put(origClass, Boolean.valueOf(false));
/* 391 */     return false;
/*     */   }
/*     */ 
/*     */   public static int deepHashCode(Object obj)
/*     */   {
/* 396 */     Set visited = new HashSet();
/* 397 */     LinkedList stack = new LinkedList();
/* 398 */     stack.addFirst(obj);
/* 399 */     int hash = 0;
/*     */ 
/* 401 */     while (!(stack.isEmpty()))
/*     */     {
/* 403 */       obj = stack.removeFirst();
/* 404 */       if (obj == null) continue; if (visited.contains(obj))
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 409 */       visited.add(obj);
/*     */ 
/* 411 */       if (obj.getClass().isArray())
/*     */       {
/* 413 */         int len = Array.getLength(obj);
/* 414 */         for (int i = 0; i < len; ++i)
/*     */         {
/* 416 */           stack.addFirst(Array.get(obj, i));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 421 */       if (obj instanceof Collection)
/*     */       {
/* 423 */         stack.addAll(0, (Collection)obj);
/*     */       }
/*     */ 
/* 427 */       if (obj instanceof Map)
/*     */       {
/* 429 */         stack.addAll(0, ((Map)obj).keySet());
/* 430 */         stack.addAll(0, ((Map)obj).values());
/*     */       }
/*     */ 
/* 434 */       if (hasCustomHashCode(obj.getClass()))
/*     */       {
/* 436 */         hash += obj.hashCode();
/*     */       }
/*     */ 
/* 440 */       Collection<Field> fields = getDeepDeclaredFields(obj.getClass());
/* 441 */       for (Field field : fields)
/*     */       {
/*     */         try
/*     */         {
/* 445 */           stack.addFirst(field.get(obj));
/*     */         }
/*     */         catch (Exception ignored)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/* 452 */     return hash;
/*     */   }
/*     */ 
/*     */   public static boolean hasCustomHashCode(Class c)
/*     */   {
/* 457 */     Class origClass = c;
/* 458 */     if (_customHash.containsKey(c))
/*     */     {
/* 460 */       return ((Boolean)_customHash.get(c)).booleanValue();
/*     */     }
/*     */ 
/* 463 */     while (!(Object.class.equals(c)))
/*     */     {
/*     */       try
/*     */       {
/* 467 */         c.getDeclaredMethod("hashCode", new Class[0]);
/* 468 */         _customHash.put(origClass, Boolean.valueOf(true));
/* 469 */         return true;
/*     */       }
/*     */       catch (Exception ignored)
/*     */       {
/*     */       }
/* 474 */       c = c.getSuperclass();
/*     */     }
/* 476 */     _customHash.put(origClass, Boolean.valueOf(false));
/* 477 */     return false;
/*     */   }
/*     */ 
/*     */   public static Collection<Field> getDeepDeclaredFields(Class c)
/*     */   {
/* 490 */     if (_reflectedFields.containsKey(c))
/*     */     {
/* 492 */       return ((Collection)_reflectedFields.get(c));
/*     */     }
/* 494 */     Collection fields = new ArrayList();
/* 495 */     Class curr = c;
/*     */ 
/* 497 */     while (curr != null)
/*     */     {
/*     */       try
/*     */       {
/* 502 */         Field[] local = curr.getDeclaredFields();
/*     */ 
/* 504 */         for (Field field : local)
/*     */         {
/* 506 */           if (!(field.isAccessible()))
/*     */           {
/*     */             try
/*     */             {
/* 510 */               field.setAccessible(true);
/*     */             }
/*     */             catch (Exception ignored)
/*     */             {
/*     */             }
/*     */           }
/*     */ 
/* 517 */           int modifiers = field.getModifiers();
/* 518 */           if ((Modifier.isStatic(modifiers)) || (field.getName().startsWith("this$")) || (Modifier.isTransient(modifiers))) {
/*     */             continue;
/*     */           }
/*     */ 
/* 522 */           fields.add(field);
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (ThreadDeath t)
/*     */       {
/* 528 */         throw t;
/*     */       }
/*     */       catch (Throwable ignored)
/*     */       {
/*     */       }
/*     */ 
/* 534 */       curr = curr.getSuperclass();
/*     */     }
/* 536 */     _reflectedFields.put(c, fields);
/* 537 */     return fields;
/*     */   }
/*     */ 
/*     */   private static class DualKey
/*     */   {
/*     */     private Object _key1;
/*     */     private Object _key2;
/*     */ 
/*     */     private DualKey()
/*     */     {
/*     */     }
/*     */ 
/*     */     private DualKey(Object k1, Object k2)
/*     */     {
/*  50 */       this._key1 = k1;
/*  51 */       this._key2 = k2;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object other)
/*     */     {
/*  56 */       if (other == null)
/*     */       {
/*  58 */         return false;
/*     */       }
/*     */ 
/*  61 */       if (!(other instanceof DualKey))
/*     */       {
/*  63 */         return false;
/*     */       }
/*     */ 
/*  66 */       DualKey that = (DualKey)other;
/*  67 */       return ((this._key1 == that._key1) && (this._key2 == that._key2));
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/*  72 */       int h1 = (this._key1 != null) ? this._key1.hashCode() : 0;
/*  73 */       int h2 = (this._key2 != null) ? this._key2.hashCode() : 0;
/*  74 */       return (h1 + h2);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.utils.DeepEquals
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */
