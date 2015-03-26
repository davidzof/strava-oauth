package org.magneato.strava;

import org.junit.Assert;
import org.junit.Test;

public class OAuth2CredentialsTest {

	@Test
	public void testStore() {
		OAuth2Credentials credentials = new OAuth2Credentials();
		credentials.setClientToken("abracadabra");
		credentials.Store();
		credentials = OAuth2Credentials.Read();
		Assert.assertEquals("abracadabra", credentials.getClientToken());
	}

}
