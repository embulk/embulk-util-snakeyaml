embulk-util-snakeyaml
======================

This is a helper on SnakeYAML for Embulk plugins.

----

Some Embulk plugins have used [SnakeYAML](https://bitbucket.org/asomov/snakeyaml/wiki/Documentation) in themselves. SnakeYAML had been included in `embulk-core`, but it is now hidden from plugins since Embulk v0.9.23.

If the plugin uses just SnakeYAML, it is sufficient just to add `org.yaml:snakeyaml:1.18` in dependencies. But if the plugin uses `org.embulk.config.YamlTagResolver`, it won't work. This helper library is to provide an alternative `org.embulk.util.snakeyaml.EmbulkYamlTagResolver` instead of `org.embulk.config.YamlTagResolver`.
