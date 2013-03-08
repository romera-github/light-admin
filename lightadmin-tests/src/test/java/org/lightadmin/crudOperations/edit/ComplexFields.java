package org.lightadmin.crudOperations.edit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lightadmin.SeleniumIntegrationTest;
import org.lightadmin.config.*;
import org.lightadmin.data.Domain;
import org.lightadmin.data.User;
import org.lightadmin.page.EditPage;
import org.lightadmin.page.ListViewPage;
import org.lightadmin.page.LoginPage;
import org.lightadmin.page.ShowViewPage;
import org.springframework.beans.factory.annotation.Autowired;

import static org.lightadmin.util.DomainAsserts.assertFieldValues;

public class ComplexFields extends SeleniumIntegrationTest {

	@Autowired
	private LoginPage loginPage;
	private ListViewPage listViewPage;
	private EditPage editPage;

	private ShowViewPage showView;

	@Before
	public void setup() {
		removeAllDomainTypeAdministrationConfigurations();

		registerDomainTypeAdministrationConfiguration( TestLineItemConfiguration.class );
		registerDomainTypeAdministrationConfiguration( TestAddressConfiguration.class );
		registerDomainTypeAdministrationConfiguration( CustomerTestEntityConfiguration.class );
		registerDomainTypeAdministrationConfiguration( OrderTestEntityWithComplexFields.class );

		listViewPage = loginPage.get().loginAs( User.ADMINISTRATOR ).navigateToDomain( Domain.TEST_ORDERS );
	}

	@After
	public void cleanup() {
		repopulateDatabase();
	}

	@Test
	public void canBeCleared() {
		clearAllFieldsAndSave();

		assertFieldValues( new String[]{ " ", " ", " ", "0" }, showView.getFieldValuesExcludingId() );
	}

	@Test
	public void selectionsCanBeReplaced() {
		replaceSelectionsAndSave();

		assertFieldValues( new String[]{ "New Customer",

				"Baker, London, United Kingdom\n" +
						"Kreschatik, Kiev, Ukraine\n" +
						"Vesterbrogade, Copenhagen, Denmark",

				"LineItem Id: 113; Product Name: Product 1\n" +
						"LineItem Id: 110; Product Name: Product 3\n" +
						"LineItem Id: 114; Product Name: Product 1",

				"19657.00" },
				showView.getFieldValuesExcludingId() );

	}

	@Test
	public void selectionCanBeAdded() {
		addSelectionsAndSave();

		assertFieldValues( new String[]{ "Carter",

				"Kreschatik, Kiev, Ukraine\n" +
						"Usteristrasse, Zurich, Switzerland\n" +
						"Via Aurelia, Rome, Italy",

				"LineItem Id: 108; Product Name: Product 1\n" +
						"LineItem Id: 109; Product Name: Product 2\n" +
						"LineItem Id: 110; Product Name: Product 3\n" +
						"LineItem Id: 114; Product Name: Product 1",

				"20671.00" },
				showView.getFieldValuesExcludingId() );
	}

	private void replaceSelectionsAndSave() {
		editPage = listViewPage.editItem( 6 );
		editPage.select( "customer", "New Customer" );

		editPage.replaceFieldSelections( "shippingAddresses",
				new String[]{ "Marksistskaya, Moscow, Russia" },
				new String[]{ "Vesterbrogade, Copenhagen, Denmark", "Baker, London, United Kingdom" } );

		editPage.replaceFieldSelections( "lineItems",
				new String[]{ "111. Product: Product 3; Amount: 6; Total: 294.00",
						"112. Product: Product 2; Amount: 12; Total: 15588.00" },
				new String[]{ "110. Product: Product 3; Amount: 4; Total: 196.00",
						"114. Product: Product 1; Amount: 7; Total: 3493.00" } );

		showView = editPage.submit();
	}


	private void addSelectionsAndSave() {
		editPage = listViewPage.editItem( 5 );

		editPage.multiSelect( "shippingAddresses", new String[]{ "Kreschatik, Kiev, Ukraine" } );
		editPage.multiSelect( "lineItems", new String[]{ "110. Product: Product 3; Amount: 4; Total: 196.00", "114. Product: Product 1; Amount: 7; Total: 3493.00" } );

		showView = editPage.submit();
	}

	private void clearAllFieldsAndSave() {
		editPage = listViewPage.editItem( 5 );

		editPage.deselect( "customer" );
		editPage.clearAllSelections( "shippingAddresses" );
		editPage.clearAllSelections( "lineItems" );

		showView = editPage.submit();
	}
}