import React from 'react';
import { Tag } from './proto/api/tag';
import { TagCard } from './components/tag/Tag';

const tag: Tag = {
    id: '1',
    name: 'Foo',
};

export function App() {
    return (
        <div>
            <h1>Hello world!</h1>
            <TagCard tag={tag} />
        </div>
    );
}
