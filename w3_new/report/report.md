# Assignment 3

Software Product Lines - Group 2

## Task 3

### Variant 1

Configuration: Logging + Commandline UI

```xml
<configuration>
	<feature automatic="selected" name="chat_application"/>
	<feature name="Authentication"/>
	<feature name="Color"/>
	<feature manual="selected" name="Logging"/>
	<feature name="Encryption"/>
	<feature name="Base64"/>
	<feature name="Reverse"/>
	<feature automatic="selected" name="UI"/>
	<feature manual="selected" name="CommandLine"/>
	<feature automatic="unselected" name="Graphical"/>
</configuration>
```

### Variant 2

Configuration: Authentication + GUI

```xml
<configuration>
	<feature automatic="selected" name="chat_application"/>
	<feature manual="selected" name="Authentication"/>
	<feature name="Color"/>
	<feature name="Logging"/>
	<feature name="Encryption"/>
	<feature name="Base64"/>
	<feature name="Reverse"/>
	<feature automatic="selected" name="UI"/>
	<feature automatic="unselected" name="CommandLine"/>
	<feature manual="selected" name="Graphical"/>
</configuration>
```

### Variant 3

Configuration: Encryption (Base64 + Reverse) + Color + GUI

```xml
<configuration>
	<feature automatic="selected" name="chat_application"/>
	<feature name="Authentication"/>
	<feature manual="selected" feature name="Color"/>
	<feature manual="selected" name="Logging"/>
	<feature automatic="selected" feature name="Encryption"/>
	<feature manual="selected" feature name="Base64"/>
	<feature manual="selected" feature name="Reverse"/>
	<feature automatic="selected" name="UI"/>
	<feature automatic="unselected" name="CommandLine"/>
	<feature manual="selected" name="Graphical"/>
</configuration>
```
