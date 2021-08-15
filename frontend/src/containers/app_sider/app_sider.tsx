import { Menu } from 'antd';
import Sider from 'antd/es/layout/Sider';
import React from 'react';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import {
    MENU_OPTION_ITEMS,
    MENU_OPTIONS,
} from '../../store/layout/menus/menu_constants';
import { selectSelectedLayoutMenuOption } from '../../store/layout/menus/menu_selectors';

export function AppSider() {
    const selectedLayoutMenuOption = useSelector(
        selectSelectedLayoutMenuOption,
    );
    return (
        <Sider width={200} className={'app-sider'}>
            <Menu
                className={'app-menu'}
                theme="dark"
                mode="vertical"
                selectedKeys={[selectedLayoutMenuOption]}
            >
                {MENU_OPTIONS.map((headerOption) => (
                    <Menu.Item key={headerOption}>
                        <Link
                            to={(location) => ({
                                ...location,
                                pathname: MENU_OPTION_ITEMS[headerOption].path,
                            })}
                        >
                            {MENU_OPTION_ITEMS[headerOption].displayName}
                        </Link>
                    </Menu.Item>
                ))}
            </Menu>
        </Sider>
    );
}
