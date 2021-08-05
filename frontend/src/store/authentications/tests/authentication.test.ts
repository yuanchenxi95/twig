import { GetUserInformationResponse } from 'proto/api/user';
import { StateObservable } from 'redux-observable';
import { Subject } from 'rxjs';
import { TestScheduler } from 'rxjs/testing';
import { MockAjaxClient } from '../../../api/ajax/mock_ajax_client';
import { getRootPersistence } from '../../../api/persistence';
import { Dependencies } from '../../dependencies';
import { authenticationGetAsyncAction } from '../authentication_actions';
import { loadUserInformationEpic } from '../authentication_epics';
import {
    AuthenticationRootState,
    authenticationState,
} from '../authentication_reducers';

describe('Authentication Store', () => {
    const testScheduler = new TestScheduler((actual, expected) => {
        expect(actual).toEqual(expected);
    });

    it('should contain the tag text', () => {
        testScheduler.run(({ hot, cold, expectObservable, flush }) => {
            const action$ = hot('-a', {
                a: authenticationGetAsyncAction.request(),
            });

            const state$ = new StateObservable<AuthenticationRootState>(
                new Subject(),
                {
                    authentication: authenticationState,
                },
            );

            const dependencies: Dependencies = {
                persistence: getRootPersistence(new MockAjaxClient()),
            };
            const getLoggedInUserInformationSpy = jest.spyOn(
                dependencies.persistence.usersPersistence,
                'getLoggedInUserInformation',
            );

            const response: GetUserInformationResponse = {
                id: '1',
                email: 'foo@example.com',
                name: 'foo',
            };
            getLoggedInUserInformationSpy.mockReturnValue(
                cold('--a', {
                    a: response,
                }),
            );

            const output$ = loadUserInformationEpic(
                action$,
                state$,
                dependencies,
            );

            const expectedAction =
                authenticationGetAsyncAction.success(response);

            expectObservable(output$).toBe('---a', {
                a: expectedAction,
            });
            flush();
            expect(getLoggedInUserInformationSpy).toBeCalledTimes(1);
        });
    });
});
