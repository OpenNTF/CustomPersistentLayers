package persistence.loader;

import persistence.client.Client;

public abstract interface ClientFactory {
	public abstract void load(String paramString);

	public abstract Client getClientInstance();
}
