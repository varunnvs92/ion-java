// Copyright (c) 2011 Amazon.com, Inc.  All rights reserved.

package com.amazon.ion.streaming;

import static com.amazon.ion.impl.IonImplUtils.intIterator;

import com.amazon.ion.InternedSymbol;
import com.amazon.ion.IonType;
import com.amazon.ion.ReaderMaker;
import com.amazon.ion.junit.Injected.Inject;
import com.amazon.ion.junit.IonAssert;
import java.io.IOException;
import java.util.Iterator;
import org.junit.Test;

/**
 *
 */
public class ReaderTest
    extends ReaderTestCase
{
    @Inject("readerMaker")
    public static final ReaderMaker[] READER_MAKERS = ReaderMaker.values();


    @Test
    public void testStepInOnNull() throws IOException
    {
        read("null.list null.sexp null.struct");

        assertEquals(IonType.LIST, in.next());
        assertTrue(in.isNullValue());
        in.stepIn();
        expectEof();
        in.stepOut();

        assertEquals(IonType.SEXP, in.next());
        assertTrue(in.isNullValue());
        in.stepIn();
        expectEof();
        in.stepOut();

        assertEquals(IonType.STRUCT, in.next());
        assertTrue(in.isNullValue());
        in.stepIn();
        expectEof();
        in.stepOut();
        expectEof();
    }

    @Test // Traps ION-133
    public void testStepOut() throws IOException
    {
        read("{a:{b:1,c:2},d:false}");

        in.next();
        in.stepIn();
        expectNextField("a");
        in.stepIn();
        expectNextField("b");
        in.stepOut(); // skip c
        expectNoCurrentValue();
        expectNextField("d");
        expectEof();
    }


    @Test
    public void testIterateTypeAnnotationIds()
    throws Exception
    {
        if (myReaderMaker.sourceIsText()) return;

        read("ann::ben::null");

        in.next();
        Iterator<Integer> typeIds = in.iterateTypeAnnotationIds();
        IonAssert.assertIteratorEquals(intIterator(10, 11), typeIds);
        expectEof();
    }

    @Test
    public void testSymbolValue()
        throws Exception
    {
        read("null.symbol sym");
        in.next();
        InternedSymbol is = in.symbolValue();
        assertEquals(null, is);
        in.next();
        is = in.symbolValue();
        assertEquals("sym", is.getText());
        // TODO sid
    }

    @Test(expected = IllegalStateException.class)
    public void testSymbolValueOnNonSymbol()
        throws Exception
    {
        read("null");
        in.next();
        in.symbolValue();
    }

    // TODO test getSymbolId() on null.symbol
}
