# Assignment 2

## Task 4

### Parameter passing

The features can be selected by passing arguments to the executable (at runtime)
and are then stored in a global (static) Configuration class. By having a global
state we don't need to pass a config to each function, which is a lot easier for
now. The configuration is immutable (it cannot not be changed at runtime), so
this approach makes the code also more maintainable and readable.

### Default features

The program arguments approach allows for a lot of flexibility for the users:
i.e. running the program with or without authentication does not require another
application, whereas global parameters would require us to compile (2^2 + 3) * 2
= 14 applications. If no features are passed, a set of default values are used:

- Logging is then enabled
- Authentication is then enabled
- The Encryption method is then set to BASE64 + Reverse

### Risks

There is a risk when selecting the `EncryptionMethod` feature: this has to be
done at both the client and server side, thus there can possibily be a mismatch.
For example, if the client selects `Reverse` as `EncryptionMethod` and the
server selects `Base64` as `EncryptionMethod`, this will result in the
applications being unable to serialize and deserialize messages.

This could be addressed by a configuration synchronization mechanism: before the
client and server start communicating they have to agree on the EncryptionMethod
that's going to be used for their communication. We currently don't address to
risk. No other invalid feature selections are possible.
