package org.jrivard.xmlchai;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class XmlFactoryTest
{

    @Test
    public void getFactory()
    {
        final XmlFactory xmlFactory = XmlFactory.getFactory();
        Assertions.assertNotNull( xmlFactory );
    }
}