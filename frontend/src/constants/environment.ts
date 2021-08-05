// Accesses the EnvironmentVariable directly for security reasons.
// https://github.com/parcel-bundler/parcel/issues/2984

import assert from 'assert';
import { switchExhaustiveError } from '../utils/switch_exhaustive';

export enum EnvironmentVariable {
    API_ENDPOINT,
}

const ENVIRONMENT_VARIABLE_MAP = new Map<EnvironmentVariable, string>();

function loadEnvironmentVariable(
    environmentVariable: EnvironmentVariable,
    value: string | undefined,
) {
    assert(value != null);
    ENVIRONMENT_VARIABLE_MAP.set(environmentVariable, value);
    return value;
}

export function getEnvironmentVariable(
    environmentVariable: EnvironmentVariable,
): string {
    const value = ENVIRONMENT_VARIABLE_MAP.get(environmentVariable);
    if (value != null) {
        return value;
    }
    switch (environmentVariable) {
        case EnvironmentVariable.API_ENDPOINT:
            return loadEnvironmentVariable(
                environmentVariable,
                process.env.API_ENDPOINT,
            );
        default:
            switchExhaustiveError();
    }
}
