import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms'; // <-- NgModel lives here
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { DataSourceComponent } from './shared/component/data-source/data-source.component';
import { TableComponent } from './shared/component/table/table.component';
import { FieldComponent } from './shared/component/field/field.component';
import { TableListComponent } from './shared/component/table-list/table-list.component';
import { DataSourceListComponent } from './shared/component/data-source-list/data-source-list.component';
import { FieldListComponent } from './shared/component/field-list/field-list.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
  declarations: [
    AppComponent,
    DataSourceComponent,
    TableComponent,
    FieldComponent,
    TableListComponent,
    DataSourceListComponent,
    FieldListComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    BrowserAnimationsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
