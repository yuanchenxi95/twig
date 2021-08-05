import { Button } from 'antd';
import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { tagListAsyncAction } from '../../store/tags/tag_actions';
import { selectTagList } from '../../store/tags/tag_selectors';
import { TagCard } from '../tag/tag_card';

export function TagList() {
    const selectedTagList = useSelector(selectTagList);
    const dispatch = useDispatch();
    return (
        <div>
            <Button onClick={() => dispatch(tagListAsyncAction.request())}>
                Load Tags
            </Button>
            <div>
                {selectedTagList.map((tag) => (
                    <TagCard key={tag.id} tag={tag} />
                ))}
            </div>
        </div>
    );
}
