module.exports = {
    useTabs: false,
    tabWidth: 4,
    singleQuote: true,
    trailingComma: 'all',
    semi: true,
    arrowParens: 'always',
    bracketSpacing: true,
    endOfLine: 'lf',
    overrides: [
        {
            files: ['*.ts'],
            options: {
                parser: 'typescript',
            },
        },
        {
            files: ['*.tsx'],
            options: {
                parser: 'typescript',
                jsxBracketSameLine: false,
                jsxSingleQuote: false,
            },
        },
        {
            files: ['*.scss'],
            options: {
                parser: 'scss',
            },
        },
        {
            files: ['*.json'],
            options: {
                parser: 'json',
            },
        },
    ],
};
