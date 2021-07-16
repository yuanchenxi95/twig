import React from 'react';
import 'jest-styled-components';
import { render } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';

import { TagCard } from '../Tag';
import { Tag } from '~/proto/api/tag';

describe('<TagCard />', () => {
    const TAG: Tag = {
        id: '1',
        name: 'foo',
    };

    it('should contain the tag text', () => {
        const tagCard = <TagCard tag={TAG} />;
        const renderedComponent = render(tagCard);
        expect(renderedComponent.queryByText(TAG.name)).toBeTruthy();
    });
});
