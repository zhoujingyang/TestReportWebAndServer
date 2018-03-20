import Vue from 'vue';
import Router from 'vue-router';
import HelloWorld from '@/components/HelloWorld';

import Home from '@/pages/home.vue';
import CreateExport from '@/pages/createReport.vue';


Vue.use(Router);

export default new Router({
    routes: [
    {
        path:'/',
        name:'home',
        component: Home
    },
    {
        path: '/createReport',
        name: 'CreateExport',
        component: CreateExport
    },
    {
        path: '/hello',
        name: 'HelloWorld',
        component: HelloWorld
    }
    ]
});
