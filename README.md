# XMLChai
XML Chai is a wrapper for Java's built-in w3c XML Library with simplified and modern API.  The Java 
w3c API predates modern APIs (like Optional) and styles (immutability), and is not thread-safe.

XML Chai API has the following features:

* Thread safe access to entire API
* Immutable mode to guarantee read-only document access
* Modern (JDK8+), simple API for common DOM functions without ceremony
* Wraps the JDK supplied w3c DOM library for correctness, efficiency and availability.
* No external dependencies outside the JDK

What XML Chai is not good at:

* Advanced/Complete XML feature access
* Performance efficiencies available when working directly with w3c API
