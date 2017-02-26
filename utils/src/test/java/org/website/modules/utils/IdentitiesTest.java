package org.website.modules.utils;

import org.junit.Test;
import org.website.modules.utils.Identities;

public class IdentitiesTest {

    @Test
    public void demo() {
        System.out.println("uuid: " + Identities.uuid());
        System.out.println("uuid2:" + Identities.uuid2());
        System.out.println("randomLong:  " + Identities.randomLong());
        System.out.println("randomBase62:" + Identities.randomBase62(7));
    }
}
