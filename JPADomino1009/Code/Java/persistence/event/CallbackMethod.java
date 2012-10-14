package persistence.event;


import persistence.event.EventListenerException;

public abstract interface CallbackMethod
{
  public abstract void invoke(Object paramObject)
    throws EventListenerException;
}

