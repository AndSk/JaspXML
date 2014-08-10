JaspXML
=======

JaspXML is just a simple XML parser for Java that behaves like an iterator. It was designed for reading data from large XML files using a simple DOM like interface, but without the memory cost of a true DOM parser or the complexity of a SAX/StAX parser.

The current implementation is build on StAX.

An example is provided in ParseAndPrint.java that show how it can be used.