import React from 'react';
import { Tag } from '~/proto/api/tag';

export function TagCard(props: { tag: Tag }) {
    return <div>{props.tag.name}</div>;
}
