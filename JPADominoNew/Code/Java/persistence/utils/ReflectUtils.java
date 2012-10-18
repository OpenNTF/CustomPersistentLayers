package persistence.utils;

/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.persistence.PersistenceException;
/*     */ 
/*     */ public class ReflectUtils
/*     */ {
/*     */   public static boolean hasInterface(Class<?> has, Class<?> in)
/*     */   {
/*  52 */     if (has.equals(in))
/*     */     {
/*  54 */       return true;
/*     */     }
/*  56 */     boolean match = false;
/*  57 */     for (Class intrface : in.getInterfaces())
/*     */     {
/*  59 */       if (intrface.getInterfaces().length > 0)
/*     */       {
/*  61 */         match = hasInterface(has, intrface);
/*     */       }
/*     */       else
/*     */       {
/*  65 */         match = intrface.equals(has);
/*     */       }
/*     */ 
/*  68 */       if (match)
/*     */       {
/*  70 */         return true;
/*     */       }
/*     */     }
/*  73 */     return false;
/*     */   }
/*     */ 
/*     */   public static Type[] getTypeArguments(Field property)
/*     */   {
/*  86 */     Type type = property.getGenericType();
/*  87 */     if (type instanceof ParameterizedType)
/*     */     {
/*  89 */       return ((ParameterizedType)type).getActualTypeArguments();
/*     */     }
/*  91 */     return null;
/*     */   }
/*     */ 
/*     */   public static boolean hasSuperClass(Class<?> has, Class<?> in)
/*     */   {
/* 106 */     if (in.equals(has))
/*     */     {
/* 108 */       return true;
/*     */     }
/* 110 */     boolean match = false;
/*     */ 
/* 112 */     if (in.getSuperclass().equals(Object.class))
/*     */     {
/* 114 */       return match;
/*     */     }
/* 116 */     match = hasSuperClass(has, in.getSuperclass());
/* 117 */     return match;
/*     */   }
/*     */ 
/*     */   public static Class<?> classForName(String className, ClassLoader classLoader)
/*     */   {
/*     */     try
/*     */     {
/* 133 */       Class c = null;
/*     */       try
/*     */       {
/* 136 */         c = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
/*     */       }
/*     */       catch (ClassNotFoundException e)
/*     */       {
/*     */         try
/*     */         {
/* 142 */           c = Class.forName(className);
/*     */         }
/*     */         catch (ClassNotFoundException e1)
/*     */         {
/* 146 */           if (classLoader == null)
/*     */           {
/* 148 */             throw e1;
/*     */           }
/*     */ 
/* 152 */           c = classLoader.loadClass(className);
/*     */         }
/*     */       }
/*     */ 
/* 156 */       return c;
/*     */     }
/*     */     catch (ClassNotFoundException e)
/*     */     {
/* 160 */       throw new PersistenceException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Class<?> stripEnhancerClass(Class<?> c)
/*     */   {
/* 173 */     String className = c.getName();
/*     */ 
/* 176 */     int enhancedIndex = className.indexOf("$$EnhancerByCGLIB");
/* 177 */     if (enhancedIndex != -1)
/*     */     {
/* 179 */       className = className.substring(0, enhancedIndex);
/*     */     }
/*     */ 
/* 182 */     if (className.equals(c.getName()))
/*     */     {
/* 184 */       return c;
/*     */     }
/*     */ 
/* 188 */     c = classForName(className, c.getClassLoader());
/*     */ 
/* 190 */     return c;
/*     */   }
/*     */ }

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.utils.ReflectUtils
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */