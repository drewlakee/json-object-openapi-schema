package io.github.jooas.adapters.openapi.yaml

class SchemaNameGenerator {
    companion object {
        fun pascalCase(value: String): String {
            val builder = StringBuilder()

            var isNextUppercase = true
            for (c in value.toCharArray()) {
                if (!c.isLetter()) {
                    isNextUppercase = true
                    continue
                }
                if (isNextUppercase) {
                    isNextUppercase = false
                    builder.append(c.uppercaseChar())
                } else {
                    builder.append(c)
                }
            }

            return builder.toString()
        }

        fun tryPascalCaseWhile(
            value: String,
            test: (String) -> Boolean,
        ): String {
            val pascalCase = pascalCase(value)
            var salt = 0
            var saltedCase: String = pascalCase
            while (test.invoke(saltedCase)) {
                saltedCase = pascalCase
                salt++
                saltedCase += salt
            }
            return if (saltedCase == pascalCase) pascalCase else saltedCase
        }
    }
}
