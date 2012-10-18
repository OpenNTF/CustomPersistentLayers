package persistence.event;

import javax.persistence.PersistenceException;



public abstract interface CallbackMethod
{
  public abstract void invoke(Object paramObject)
    throws PersistenceException;
}

