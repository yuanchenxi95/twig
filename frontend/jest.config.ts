import type { Config } from '@jest/types';
import { defaults } from 'jest-config';

const JEST_CONFIG: Config.InitialOptions = {
    moduleFileExtensions: [...defaults.moduleFileExtensions, 'ts', 'tsx'],
    testRegex: 'tests/.*\\.test\\.(ts|tsx)$',
    testEnvironment: 'jsdom',
    moduleNameMapper: {
        '^proto/(.*)$': '<rootDir>/src/proto/$1',
    },
};

export default JEST_CONFIG;
