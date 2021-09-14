Two design pattern
==================

Observer pattern
----------------

In the Server (Server.java) we have implemented the observer mattern.  We kind
of had this last week but did not use a formal observer and observable
interface. So to properly implement this design pattern we made Server the
Observable. Any message that is send by a client can be observed via the server.
A client is then a Observer of the server once the client has authenticated.
This allows for a more decoupled approach. For example it's now easier to test a
client and server seperately since a part of their communication only happens
via the observer/observable pattern.

Command pattern
---------------

We already have implemented the command payttern during the last assignment. The
"Message", "MessageColor" and "MessageType" class/enums are the commands. We had
implemented this already for the obvious reason that it makes it easy to share
data between the client and server. It also allows to relatively easily add new
behaviour to the app by adding additional "MessageType" enum values.
