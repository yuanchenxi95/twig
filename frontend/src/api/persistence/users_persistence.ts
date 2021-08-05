import { GetUserInformationResponse } from 'proto/api/user';
import { map, Observable } from 'rxjs';
import { AjaxClient } from '../ajax/ajax_client';

export class UsersPersistence {
    constructor(private readonly ajaxClient: AjaxClient) {}

    getLoggedInUserInformation(): Observable<GetUserInformationResponse> {
        return this.ajaxClient
            .get<GetUserInformationResponse>('/api/users/me')
            .pipe(
                map((response) =>
                    GetUserInformationResponse.fromPartial(response),
                ),
            );
    }
}
