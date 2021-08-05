import '@testing-library/jest-dom/extend-expect';
import { render } from '@testing-library/react';
import 'jest-styled-components';
import { Tag } from 'proto/api/tag';
import React from 'react';

import { TagCard } from '../tag_card';

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
