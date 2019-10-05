import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DataSourceComponent } from './shared/component/data-source/data-source.component';
import { FieldComponent } from './shared/component/field/field.component';
import { TableComponent } from './shared/component/table/table.component';

const routes: Routes = [
  { path: 'dataSource1', component: DataSourceComponent},
  {path: 'table1', component: TableComponent},
  {path: 'field1', component: FieldComponent}
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
